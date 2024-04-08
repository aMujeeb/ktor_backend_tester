package lk.mujtestktor.data

import lk.mujtestktor.data.modal.Note
import lk.mujtestktor.data.modal.User

interface NoteDataAccessObject { // Main purpose is if need to unit test back end. acts as an facilitator
    //Adding signatures for functions

    suspend fun checkPasswordForEmail(email: String, password: String): Boolean
    suspend fun checkIfEmailExists(email: String): Boolean
    suspend fun registerUser(user: User): Boolean

    suspend fun getUserIdWithEmail(email: String): String

    suspend fun getAllNotesOfUser(userID: String): List<Note>

    suspend fun insertNote(note: Note): Boolean

    suspend fun updateNote(note: Note): Boolean

    suspend fun checkIfNoteExists(noteID: String): Boolean

    suspend fun isNoteOwnedBy(noteId: String, userId: String): Boolean

    suspend fun deleteNote(noteId: String): Boolean
}