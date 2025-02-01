package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Features
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

fun Routing.featureService(db: Database, httpClient: HttpClient) {
    webSocket("feature/liveFeature") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.featuresQueries.selectAll(receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val feature = gson.toJson(it.executeAsOne())

                        //Send the converted json.
                        send(Frame.Text(feature.toString().clean()))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("feature/liveFeatureChoices") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.featureChoiceEntityQueries.selectAllFor(
                        featureId = receivedText.toLong(),
                        owner = userInfo.id
                    ).asFlow().collect {
                        val feature = gson.toJson(it.executeAsList())

                        //Send the converted json.
                        send(Frame.Text(feature.toString().clean()))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("feature/liveFeatureSpells") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.featureSpellCrossRefQueries.selectSpellsForFeature(
                        featureId = receivedText.toLong(),
                        owner = userInfo.id
                    ).asFlow().collect {
                        val feature = gson.toJson(it.executeAsList())

                        //Send the converted json.
                        send(Frame.Text(feature.toString().clean()))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("featurel/liveAllIndexes") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.indexRefQueries.selectAll(
                owner = userInfo.id
            ).asFlow().collect {
                val spells = gson.toJson(it.executeAsList())

                //Send the converted json.
                send(Frame.Text(spells.toString().clean()))
            }
        }
    }




    post("feature/insertFeature") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val feature = gson.fromJson(response, Features::class.java)
            db.featuresQueries.insert(feature.copy(owner = userInfo.id))
            call.respondText(feature.featureId.toString())
        }
    }

    post("feature/insertFeatureSpell") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.featureSpellCrossRefQueries.insert(
                featureId = body.getLong("featureId"),
                spellId = body.getLong("spellId"),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Inserted")
        }
    }

    post("feature/insertFeatureOptions") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.featureOptionsCrossRefQueries.insert(
                featureId = body.getLong("featureId"),
                id = body.getLong("id"),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Inserted")
        }
    }

    post("feature/insertFeatureChoice") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.featureChoiceEntityQueries.insert(
                id = body.getLong("id"),
                choose = body.getString("choose"),
                owner = userInfo.id
            )
            call.respondText(body.getLong("id").toString())
        }
    }

    post("feature/insertIndex") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.indexRefQueries.insert(
                owner = userInfo.id,
                index = body.getString("index"),
                ids = body.getJSONArray("ids").toString()
            )
            call.respondText(body.getLong("id").toString())
        }
    }

    post("feature/insertOptionsFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.optionsFeatureCrossRefQueries.insertOptionsFeatureCrossRef(
                owner = userInfo.id,
                featureId = body.getLong("featureId"),
                choiceId = body.getLong("choiceId"),
            )
            call.respondText(body.getLong("id").toString())
        }
    }

    post("feature/insertFeatureChoiceIndex") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.featureChoiceIndexCrossRefQueries.insert(
                owner = userInfo.id,
                choiceId = body.getLong("choiceId"),
                index = body.getString("index"),
                levels = body.getString("levels"),
                classes = body.getString("classes"),
                schools = body.getString("schools"),
            )
            call.respondText(body.getLong("id").toString())
        }
    }

    post("feature/removeIdFromRef") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            //Fetch the old version from the database.
            val oldRef = db.indexRefQueries.select(
                index = body.getString("ref"),
                owner = userInfo.id
            ).executeAsOne()

            //Remove the requested id.
            val refJson = JSONArray(oldRef)
            refJson.removeAll { it == body.getLong("id") }

            //Update the database with the new version.
            db.indexRefQueries.insert(
                index = body.getString("index"),
                ids = refJson.toString(),
                owner = userInfo.id
            )

            //Respond to the call.
            call.respondText("removed")
        }
    }



    get("feature/featureChoices") {
        withUserInfo { userInfo: UserInfo ->
            val value = db.featureChoiceEntityQueries.selectAllFor(
                owner = userInfo.id,
                featureId = call.parameters["featureId"]!!.toLong()
            ).executeAsList()
            call.respond(gson.toJson(value))
        }
    }

    get("feature/featureSpells") {
        withUserInfo { userInfo: UserInfo ->
            val value = db.featureSpellCrossRefQueries.selectSpellsForFeature(
                owner = userInfo.id,
                featureId = call.parameters["featureId"]!!.toLong()
            ).executeAsList()
            call.respond(gson.toJson(value))
        }
    }

    get("feature/getFeatureIdFromSpell") {
        withUserInfo { userInfo: UserInfo ->
            val value = db.featureSpellCrossRefQueries.selectFeatureForSpell(
                owner = userInfo.id,
                id = call.parameters["id"]!!.toLong()
            ).executeAsList()
            call.respond(gson.toJson(value))
        }
    }

    get("feature/featureChoiceOptions") {
        //TODO test this thoroughly and reread the sql.
        withUserInfo { userInfo: UserInfo ->
            val value = db.featuresQueries.selectFeatureOptions(
                owner = userInfo.id,
                featureChoiceId = call.parameters["featureChoiceId"]!!.toLong()
            ).executeAsList()
            call.respond(gson.toJson(value))
        }
    }




    delete("feature/clearFeatureChoiceIndexRefs") {
        withUserInfo { userInfo ->
            db.featureChoiceIndexCrossRefQueries.clearForId(
                id = call.parameters["id"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("feature/deleteFeatureFeatureChoice") {
        withUserInfo { userInfo ->
            db.featureChoiceChoiceEntityQueries.delete(
                characterId = call.parameters["characterId"]!!.toLong(),
                choiceId = call.parameters["choiceId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("feature/deleteFeatureOptions") {
        withUserInfo { userInfo ->
            db.featureOptionsCrossRefQueries.delete(
                id = call.parameters["id"]!!.toLong(),
                owner = userInfo.id,
                featureId = call.parameters["featureId"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("feature/deleteOptionsFeature") {
        withUserInfo { userInfo ->
            db.optionsFeatureCrossRefQueries.insertOptionsFeatureCrossRef(
                choiceId = call.parameters["choiceId"]!!.toLong(),
                owner = userInfo.id,
                featureId = call.parameters["featureId"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("feature/removeFeatureSpell") {
        withUserInfo { userInfo ->
            db.featureSpellCrossRefQueries.delete(
                spellId = call.parameters["spellId"]!!.toLong(),
                featureId = call.parameters["featureId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }
}