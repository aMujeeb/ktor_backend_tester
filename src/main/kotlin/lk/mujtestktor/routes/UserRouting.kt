package lk.mujtestktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import lk.mujtestktor.data.modal.User
import lk.mujtestktor.data.requests.UserLoginRequest
import lk.mujtestktor.data.requests.UserRegisterRequest
import lk.mujtestktor.dataBase
import lk.mujtestktor.util.getHashWithSalt
import kotlin.math.round

fun Route.userRoutes() {
    route("/register") {
        post {
            val userRegisterRequest = try {
                call.receive<UserRegisterRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userEmail = userRegisterRequest.email
            if (dataBase.checkIfEmailExists(userEmail)) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            val emailPattern = ("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$").toRegex()
            if (!emailPattern.matches(userEmail)) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userPassword = userRegisterRequest.password
            val hashPassword = getHashWithSalt(userPassword)

            if (dataBase.registerUser(User(userEmail, userRegisterRequest.username, hashPassword))) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }

    route("/login") {
        post {
            val userLoginRequest = try {
                call.receive<UserLoginRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (dataBase.checkPasswordForEmail(userLoginRequest.email, userLoginRequest.password)) {
                call.respond(HttpStatusCode.OK)
                return@post
            }

            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}