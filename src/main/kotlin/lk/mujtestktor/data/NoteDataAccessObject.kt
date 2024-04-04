package lk.mujtestktor.data

import lk.mujtestktor.data.modal.User

interface NoteDataAccessObject { // Main purpose is if need to unit test back end. acts as an facilitator
    //Adding signatures for functions

    suspend fun checkPasswordForEmail(email: String, password: String): Boolean
    suspend fun checkIfEmailExists(email: String): Boolean
    suspend fun registerUser(user: User): Boolean
}