package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Subraces
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

fun Routing.subraceService(db: Database, httpClient: HttpClient) {

    post("subrace/insertSubraceFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.subraceFeatureCrossRefQueries.insert(
                subraceId = body.getLong("subraceId"),
                featureId = body.getLong("featureId"),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Inserted")
        }
    }

    post("subrace/insertSubrace") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val subraces = gson.fromJson(response, Subraces::class.java)
            val newId = if(subraces.id <= 0) {
                (db.subracesQueries.selectHighestIdForOwner(userInfo.id).executeAsOne().max ?: 0) + 1
            } else {
                subraces.id
            }
            db.subracesQueries.insert(subraces.copy(owner = userInfo.id, id = newId))
            call.respondText(newId.toString())
        }
    }



    delete("subrace/removeSubraceFeature") {
        withUserInfo { userInfo ->
            db.subraceFeatureCrossRefQueries.delete(
                subraceId = call.parameters["subraceId"]!!.toLong(),
                featureId = call.parameters["featureId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond("Removed")
        }
    }

    delete("subrace/removeRaceSubrace") {
        withUserInfo { userInfo ->
            db.raceSubraceCrossRefQueries.delete(
                subraceId = call.parameters["subraceId"]!!.toLong(),
                raceId = call.parameters["raceId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond("Removed")
        }
    }

    delete("subrace/deleteSubrace") {
        withUserInfo { userInfo ->
            db.subracesQueries.delete(
                id = call.parameters["id"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond("Deleted")
        }
    }




    webSocket("subrace/getLiveSubrace") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subracesQueries.selectById(receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val item = gson.toJson(it.executeAsOne())

                        //Send the converted json.
                        send(Frame.Text(item))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }


    webSocket("subrace/liveHomebrewSubraces") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.subracesQueries.selectHomebrewByOwner(owner = userInfo.id).asFlow().collect {
                val item = gson.toJson(it.executeAsList())

                //Send the converted json.
                send(Frame.Text(item))
            }
        }
    }

    webSocket("subrace/liveSubraceFeatures") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subraceFeatureCrossRefQueries.selectFeaturesBySubrace(
                        subraceId = receivedText.toLong(),
                        owner = userInfo.id
                    ).asFlow().collect {
                        val item = gson.toJson(it.executeAsList())

                        //Send the converted json.
                        send(Frame.Text(item))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("subrace/liveFilledSubraces") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)

            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val id = frame.readText().toLong()


                val list = JSONArray()
                db.raceSubraceCrossRefQueries.selectSubraceForRace(id, userInfo.id).executeAsList().forEach { subrace ->
                    val json = JSONObject(
                        gson.toJson(subrace)
                    )

                    val features = db.subraceFeatureCrossRefQueries.selectFeaturesBySubrace(
                        owner = userInfo.id,
                        subraceId = subrace.id
                    ).executeAsList()

                    val featChoices = db.subraceFeatChoiceCrossRefQueries.selectFeatChoicesForSubrace(
                        owner = userInfo.id,
                        id = subrace.id
                    ).executeAsList()

                    json.put("traits", JSONArray(gson.toJson(features)))
                    val filledFeatChoices = JSONArray()
                    featChoices.forEach { choiceRef ->
                        val choice = db.featChoicesQueries.select(
                            id = choiceRef.id,
                            owner = choiceRef.owner
                        ).executeAsOne()
                        val from = db.featChoiceFeatCrossRefQueries.selectFeatsForChoice(
                            owner = choiceRef.owner,
                            featChoiceId = choiceRef.featChoiceId
                        ).executeAsList()
                        val jsonObject = JSONObject()
                        jsonObject.put("name", choice.name)
                        jsonObject.put("choose", choice.choose)
                        jsonObject.put("from", JSONArray(gsonInstance.toJson(from)))
                        filledFeatChoices.put(jsonObject)
                    }

                    json.put("featChoices", filledFeatChoices)

                    list.put(json)
                }
                send(list.toString())
            }

        }
    }
}