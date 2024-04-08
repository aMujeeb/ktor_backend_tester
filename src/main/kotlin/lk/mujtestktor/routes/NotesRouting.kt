package lk.mujtestktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import lk.mujtestktor.data.modal.Note
import lk.mujtestktor.data.requests.NoteRequest
import lk.mujtestktor.dataBase

fun Route.noteRouts() {
    route("/notes") {
        //First need to check is User Authenticated
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name //This is because using Basic Authentication

                call.respond(HttpStatusCode.OK, dataBase.getAllNotesOfUser(dataBase.getUserIdWithEmail(email)))
            }

            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val noteData = try {
                    call.receive<NoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val note = Note(
                    title = noteData.title,
                    text = noteData.text,
                    timestamp = System.currentTimeMillis(),
                    owner = dataBase.getUserIdWithEmail(email)
                )

                dataBase.insertNote(note)
                call.respond(HttpStatusCode.OK, note)
            }

            //Update scenario
            patch {
                val email = call.principal<UserIdPrincipal>()!!.name
                val noteData = try {
                    call.receive<NoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }

                val noteId = call.request.queryParameters["id"] ?: ""
                if (noteId == "") {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }

                if (!dataBase.checkIfNoteExists(noteId)) {
                    call.respond(HttpStatusCode.NotFound)
                    return@patch
                }

                if (dataBase.isNoteOwnedBy(noteId, dataBase.getUserIdWithEmail(email))) {
                    val updatingNote = Note(title = noteData.title, text = noteData.text, id = noteId)

                    dataBase.updateNote(updatingNote)
                    call.respond(HttpStatusCode.OK, updatingNote)
                    return@patch
                } else {
                    call.respond(HttpStatusCode.Forbidden)
                    return@patch
                }
            }

            delete {
                val email = call.principal<UserIdPrincipal>()!!.name
                val noteId = call.request.queryParameters["id"] ?: ""

                if (noteId == "") {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if (dataBase.isNoteOwnedBy(noteId, dataBase.getUserIdWithEmail(email))) {
                    dataBase.deleteNote(noteId)
                    call.respond(HttpStatusCode.OK)
                    return@delete
                } else {
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }
            }
        }
    }
}