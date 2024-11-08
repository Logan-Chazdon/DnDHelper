package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Backgrounds
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.json.JSONObject

fun Routing.backgroundService(db: Database, httpClient: HttpClient) {
    get("background/allBackgrounds") {
        val session = getSession(call)
        val userInfo = session?.let { getUserInfo(httpClient, it, call) }
        val query = db.backgroundsQueries.selectBackgroundsFor(userInfo?.id).executeAsList()

        call.respondText(gson.toJson(query).clean())
    }

    get("background/backgroundSpells") {
        withUserInfo { userInfo: UserInfo ->
            val id = call.parameters["backgroundId"]!!.toLong()
            val data = db.backgroundSpellCrossRefQueries.selectSpellsForBackground(
                backgroundId = id,
                owner = userInfo.id
            )
            call.respondText(gson.toJson(data))
        }
    }

    get("background/backgroundFeatures") {
        withUserInfo { userInfo: UserInfo ->
            val id = call.parameters["backgroundId"]!!.toLong()
            val data = db.backgroundFeatureCrossRefQueries.selectFeaturesForBackground(
                backgroundId = id,
                owner = userInfo.id
            )
            call.respondText(gson.toJson(data))
        }
    }





    post("background/insertBackground") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val background = gson.fromJson(response, Backgrounds::class.java)
            db.backgroundsQueries.insert(background.copy(owner = userInfo.id))
            call.respondText(background.id.toString(), status = HttpStatusCode.OK)
        }
    }

    post("background/insertBackgroundSpell") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())

            db.backgroundSpellCrossRefQueries.insert(
                spellId = body.getLong("spellId"),
                backgroundId = body.getLong("backgroundId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Inserted")
        }
    }

    post("background/insertBackgroundFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())

            db.backgroundFeatureCrossRefQueries.insert(
                featureId = body.getLong("featureId"),
                backgroundId = body.getLong("backgroundId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Inserted")
        }
    }





    delete("background/deleteBackground") {
        withUserInfo { userInfo ->
            db.backgroundsQueries.delete(
                owner = userInfo.id,
                id = call.parameters["id"]!!.toLong(),
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }



    webSocket("background/homebrewBackgrounds") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.backgroundsQueries.selectHomebrewBackgounds(userInfo.id).asFlow().collect { x ->
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

    webSocket("background/backgroundEntity") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    val id = receivedText.toLong()
                    db.backgroundsQueries.selectBackground(
                        owner= userInfo.id,
                        id = id
                    ).asFlow().collect { x ->
                        send(gson.toJson(x.executeAsList()).clean())
                    }
                } catch(e: NumberFormatException) {
                    send("Invalid Id")
                }
            }
        }
    }
}