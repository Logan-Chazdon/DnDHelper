package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Characters
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.clean
import gmail.loganchazdon.dndhelper.model.database.gson
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*


fun Routing.characterService(db: Database) {
    post("character/postCharacter") {
        val character = call.receive<Characters>()
        db.characterQueries.insertOrReplace(character)
        call.respondText(
            db.characterQueries.lastInsertRowId().executeAsOne().toString()
        )
    }
    get("character/{id}") {

    }
    delete("character/deleteCharacter") {
        call.parameters["id"]?.let { db.characterQueries.delete(it.toLong()) }
         call.respond(HttpStatusCode.OK, "Item deleted")
    }
    get("character/getAllCharacters") {
        val list = db.characterQueries.selectAll().executeAsList()
        gson.toJson(list)
        call.respondText(gson.toJson(list).clean())
    }
    webSocket("character/getLiveCharacters") {
        send("[]")
        db.characterQueries.selectAll().asFlow().collect { x ->
            send(gson.toJson(x.executeAsList()).clean())
        }
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            if (receivedText.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            } else {
                send(Frame.Text("received"))
            }
        }
    }
}
