package lk.mujtestktor.data

import lk.mujtestktor.data.modal.Note
import lk.mujtestktor.data.modal.User
import lk.mujtestktor.util.checkHashForPassword
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

class NotesDataBase : NoteDataAccessObject { //Main class for Mongo data base. Connectivity class
    private val client = KMongo.createClient().coroutine

    private val dataBase = client.getDatabase("NoteDataBase") // Name of database in Mongo

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
}