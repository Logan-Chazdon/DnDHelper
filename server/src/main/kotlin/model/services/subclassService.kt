package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Subclasses
import gmail.loganchazdon.dndhelper.model.database.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.json.JSONArray
import org.json.JSONObject

private fun deserializeSubclass(text: String, owner: String): Subclasses {
    val json = JSONObject(text)
    return Subclasses(
        subclass_name = json.getString("name"),
        subclass_spell_casting = if (json.has("spell_casting"))
            jsonObjectAdapter.decode(json.optString("spell_casting")) else null,
        subclass_isHomebrew = json.getLong("isHomebrew"),
        subclassId = json.getLong("subclassId"),
        spellAreFree = json.getBoolean("spellAreFree"),
        owner = owner
    )
}

private fun serializeSubclass(subclass: Subclasses): JSONObject {
    val json = JSONObject()
    json.put("name", subclass.subclass_name)
    json.put("spellCasting", subclass.subclass_spell_casting)
    json.put("isHomebrew", subclass.subclass_isHomebrew)
    json.put("spellAreFree", subclass.spellAreFree)
    json.put("subclassId", subclass.subclassId)

    return json
}

private fun serializeSubclassList(list: List<Subclasses>): String {
    val json = JSONArray()
    list.forEach {
        json.put(serializeSubclass(it))
    }
    return json.toString()
}


fun Routing.subclassService(db: Database, httpClient: HttpClient) {

    post("subclass/insertSubclass") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val subclass = deserializeSubclass(response, userInfo.id)
            val newId = if(subclass.subclassId <= 0) {
                (db.subclassesQueries.selectHighestIdForOwner(userInfo.id).executeAsOne().max ?: 0) + 1
            } else {
                subclass.subclassId
            }
            db.subclassesQueries.insert(subclass.copy(owner = userInfo.id, subclassId = newId))
            call.respondText(newId.toString())
        }
    }

    post("subclass/insertSubclassFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.subclassFeatureCrossRefQueries.insert(
                subclassId = body.getLong("subclassId"),
                featureId = body.getLong("featureId"),
                owner = userInfo.id
            )
            call.respondText("Inserted")
        }
    }




    delete("subclass/removeSubclassFeature") {
        withUserInfo { userInfo ->
            db.subclassFeatureCrossRefQueries.delete(
                subclassId = call.parameters["subclassId"]!!.toLong(),
                featureId = call.parameters["featureId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("subclass/deleteSubclass") {
        withUserInfo { userInfo ->
            db.subclassesQueries.delete(
                subclassId = call.parameters["subclassId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }





    get("subclass/subclassFeatures") {
        withUserInfo { userInfo: UserInfo ->
            val query = db.subclassFeatureCrossRefQueries.selectFeaturesBySubclass(
                owner = userInfo.id,
                subclassId = call.parameters["subclassId"]!!.toLong(),
                level = call.parameters["maxLevel"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, gson.toJson(query.executeAsList()))
        }
    }




    webSocket("subclass/subclassesById") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subclassesQueries.selectByClass(classId = receivedText.toLong(), owner = userInfo.id).asFlow()
                        .collect {
                            val subclasses = serializeSubclassList(it.executeAsList())

                            //Send the converted json.
                            send(Frame.Text(subclasses))
                        }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("subclass/subclassLiveFeatures") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subclassFeatureCrossRefQueries.selectFeaturesBySubclass(
                        owner = userInfo.id,
                        subclassId = receivedText.toLong(),
                        20
                    ).asFlow().collect {
                        val features = gson.toJson(it.executeAsList())

                        //Send the converted json.
                        send(Frame.Text(features))
                    }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("subclass/homebrewSubclasses") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.subclassesQueries.selectHomebrewSubclasses(owner = userInfo.id).asFlow().collect {
                val subclasses = serializeSubclassList(it.executeAsList())
                //Send the converted json.
                send(Frame.Text(subclasses))
            }
        }
    }

    webSocket("subclass/liveSubclass") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subclassesQueries.selectSubclass(id = receivedText.toLong(), owner = userInfo.id).asFlow()
                        .collect {
                            val subclass = serializeSubclass(it.executeAsOne()).toString()

                            //Send the converted json.
                            send(Frame.Text(subclass))
                        }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }
}