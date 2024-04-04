package lk.mujtestktor

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(DefaultHeaders) //Installing default headers
    install(CallLogging) {
        level = Level.INFO
    }

    install(Authentication) {

    }

    //configureRouting()
    install(Routing) {//Handle all Routing functions(GET,POST..etc)
        route("/"){
            get {
                call.respond("Hello World")
            }
        }
    }
}
