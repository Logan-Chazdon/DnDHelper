package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Spells
import gmail.loganchazdon.dndhelper.model.database.*
import io.ktor.client.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.json.JSONObject

fun Routing.spellService(db: Database, httpClient: HttpClient) {
    post("spell/insertSpell") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val spell = gson.fromJson(response, Spells::class.java)
            db.spellsQueries.insertSpell(spell.copy(owner = userInfo.id))
            call.respondText(spell.id.toString())
        }
    }

    post("spell/addClassSpell") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())

            db.classSpellCrossRefQueries.insert(
                classId = body.getLong("classId"),
                spellId = body.getLong("spellId"),
                owner = userInfo.id
            )
            call.respondText("Added")
        }
    }





    delete("spell/removeClassSpell") {
        withUserInfo { userInfo ->
            db.classSpellCrossRefQueries.delete(
                owner = userInfo.id,
                spellId = call.parameters["spellId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong()
            )

            call.respond("Item deleted")
        }
    }

    delete("spell/deleteSpell") {
        withUserInfo { userInfo ->
            db.spellsQueries.delete(
                id = call.parameters["id"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond("Item deleted")
        }
    }





    webSocket("spell/allSpellsLive") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.spellsQueries.selectAllFor(userInfo.id).asFlow().collect { x ->
                send(gson.toJson(x.executeAsList()).clean())
            }
        }
    }

    webSocket("spell/liveSpellClasses") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)


            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val id = frame.readText().toLong()

                db.classSpellCrossRefQueries.selectClassesForSpell(
                    userInfo.id,
                    spellId = id
                ).asFlow().collect { x ->
                    send(gson.toJson(x.executeAsList()).clean())
                }

            }
        }
    }

    webSocket("spell/homebrewSpellsLive") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.spellsQueries.selectHomebrewFor(userInfo.id).asFlow().collect { x ->
                send(gson.toJson(x.executeAsList()).clean())
            }
        }
    }

    webSocket("spell/liveSpell") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val id = frame.readText().toLong()

                db.spellsQueries.selectById(
                    userInfo.id,
                    spellId = id
                ).asFlow().collect { x ->
                    send(gson.toJson(x.executeAsOne()))
                }
            }
        }
    }
}