package lk.mujtestktor.data.requests

data class UserRegisterRequest(
    val email: String,
    val password: String,
    val username: String
)
