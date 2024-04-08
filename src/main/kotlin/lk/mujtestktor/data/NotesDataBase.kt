package lk.mujtestktor.data

import com.mongodb.ConnectionString
import lk.mujtestktor.data.modal.Note
import lk.mujtestktor.data.modal.User
import lk.mujtestktor.util.checkHashForPassword
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

class NotesDataBase : NoteDataAccessObject { //Main class for Mongo data base. Connectivity class
    private val client =
        KMongo.createClient(ConnectionString("<Mongo DB Path>")).coroutine

    private val dataBase =
        client.getDatabase("NoteDataBase") // Name of database in Mongo if not exists it will create one at runtime

    //No tables having collections
    private val users = dataBase.getCollection<User>()
    private val notes = dataBase.getCollection<Note>()

    override suspend fun checkPasswordForEmail(email: String, password: String): Boolean {
        val actualPassword = users.findOne(User::email eq email)?.password
            ?: return false //eq is equal check is there a password available for the given email
        //Passwords are hashed. In order need some mechanism to compare
        return checkHashForPassword(password, actualPassword)
    }

    override suspend fun checkIfEmailExists(email: String): Boolean {
        return users.findOne(User::email eq email) != null
    }

    override suspend fun registerUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserIdWithEmail(email: String): String {
        return users.findOne(User::email eq email)?.id ?: ""
    }

    override suspend fun getAllNotesOfUser(userID: String): List<Note> {
        return notes.find(Note::owner eq userID).toList()
    }

    override suspend fun insertNote(note: Note): Boolean {
        return notes.insertOne(note).wasAcknowledged()
    }

    override suspend fun updateNote(note: Note): Boolean {
        notes.updateOneById(note.id, setValue(Note::title, note.title))
        notes.updateOneById(note.id, setValue(Note::text, note.text))
        return notes.updateOneById(note.id, setValue(Note::timestamp, System.currentTimeMillis())).wasAcknowledged()
    }

    override suspend fun checkIfNoteExists(noteID: String): Boolean {
        return notes.findOneById(noteID) != null
    }

    override suspend fun isNoteOwnedBy(noteId: String, userId: String): Boolean {
        return notes.findOneById(noteId)?.owner == userId
    }

    override suspend fun deleteNote(noteId: String): Boolean {
        return notes.deleteOneById(noteId).wasAcknowledged()
    }
}