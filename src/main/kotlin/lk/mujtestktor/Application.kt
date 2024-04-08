package lk.mujtestktor

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import lk.mujtestktor.data.NoteDataAccessObject
import lk.mujtestktor.data.NotesDataBase
import lk.mujtestktor.routes.noteRouts
import lk.mujtestktor.routes.userRoutes
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

//Reference to Database
val dataBase: NoteDataAccessObject = NotesDataBase()

fun Application.module() {

    install(DefaultHeaders) //Installing default headers
    install(CallLogging) {
        level = Level.INFO
    }

    //Define JSON as request formats
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(Authentication) {
        configureAuth()
    }

    //configureRouting()
    install(Routing) {//Handle all Routing functions(GET,POST..etc)
        route("/") {
            get {
                call.respond("Hello World")
            }

            userRoutes()
            noteRouts()
        }
    }
}

//The authentication process
private fun AuthenticationConfig.configureAuth() {
    basic { //Can use either JWT, oAuth etc... Basic is the common
        realm = "Notes Server" //Any name
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password
            if (dataBase.checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email) //Is already available user
            } else {
                null //Not logged
            }
        }
    }
}
