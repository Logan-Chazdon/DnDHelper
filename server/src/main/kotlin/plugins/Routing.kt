package gmail.loganchazdon.dndhelper.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        options("/*") { // Handle preflight requests
            call.respond(HttpStatusCode.OK)
        }
    }
}