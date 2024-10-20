package gmail.loganchazdon.dndhelper.model.services

import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.clean
import gmail.loganchazdon.dndhelper.model.database.gson
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.classService(db: Database) {
    get("class/getAllClasses") {
        call.respondText(gson.toJson(db.classesQueries.getAllClasses().executeAsList()).clean())
    }
    get("class/getUnfilledClass/{id}") {
        try {
            val id = call.parameters["id"]!!.toLong()
            call.respondText(gson.toJson(db.classesQueries.getClassById(id).executeAsOne()).clean())
        } catch(e: Exception) {
            call.respond(HttpStatusCode(400, "Invalid Id"))
        }
    }
}
