package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Races
import gmail.loganchazdon.dndhelper.model.database.*
import gmail.loganchazdon.dndhelper.model.database.utils.fillOutFeatureListWithoutChosen
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.http.cio.internals.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.json.JSONObject

fun Routing.raceService(db: Database, httpClient: HttpClient) {

    post("race/insertRace") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val race = gson.fromJson(response, Races::class.java)
            val newId = if (race.raceId <= 0) {
                (db.racesQueries.selectHighestIdForOwner(userInfo.id).executeAsOne().max ?: 0).orMinimum() + 1
            } else {
                race.raceId
            }
            db.racesQueries.insertRace(race.copy(owner = userInfo.id, raceId = newId))
            call.respondText(race.raceId.toString(), status = HttpStatusCode.OK)
        }
    }

    post("race/insertRaceFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.raceFeatureCrossRefQueries.insert(
                featureId = body.getLong("featureId"),
                raceId = body.getLong("raceId"),
                owner = userInfo.id
            )
            call.respondText("Inserted", status = HttpStatusCode.OK)
        }
    }

    post("race/insertRaceSubrace") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.raceSubraceCrossRefQueries.insert(
                subraceId = body.getLong("subraceId"),
                raceId = body.getLong("raceId"),
                owner = userInfo.id
            )
            call.respondText("Inserted", status = HttpStatusCode.OK)
        }
    }






    delete("race/deleteRaceFeature") {
        withUserInfo { userInfo ->
            db.raceFeatureCrossRefQueries.delete(
                owner = userInfo.id,
                raceId = call.parameters["raceId"]!!.toLong(),
                featureId = call.parameters["featureId"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("race/deleteRace") {
        withUserInfo { userInfo ->
            db.racesQueries.delete(
                owner = userInfo.id,
                raceId = call.parameters["id"]!!.toLong(),
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }






    get("race/raceFeatures") {
        withUserInfo {
            call.respondText(
                db.fillOutFeatureListWithoutChosen(
                    db.racesQueries.selectRaceFeatures(
                        it.id,
                        raceId = call.parameters["raceId"]!!.toLong()
                    ).executeAsList(), it.id
                ).toString()
            )
        }
    }

    get("race/subraceFeatures") {
        withUserInfo {
            call.respondText(
                db.fillOutFeatureListWithoutChosen(
                    db.subracesQueries.selectSubraceFeatures(
                        it.id,
                        subraceId = call.parameters["subraceId"]!!.toLong()
                    ).executeAsList(),  it.id
                ).toString()
            )

        }
    }

    get("race/subraceFeatChoices") {
        withUserInfo {
            call.respondText(
                gson.toJson(
                    db.subraceFeatChoiceCrossRefQueries.selectFeatChoicesForSubrace(
                        it.id,
                        id = call.parameters["id"]!!.toLong()
                    ).executeAsList()
                )
            )
        }
    }

    get("race/allRaces") {
        withUserInfo {
            call.respondText(
                gson.toJson(
                    db.racesQueries.getRacesFor(
                        it.id,
                    ).executeAsList()
                )
            )
        }
    }

    get("race/homebrewRaces") {
        withUserInfo {
            call.respondText(
                gson.toJson(
                    db.racesQueries.selectHomebrewRaces(
                        it.id
                    ).executeAsList()
                )
            )
        }
    }




    webSocket("race/getLiveRace") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText: String = frame.readText()
                try {
                    db.racesQueries.getRace(id = receivedText.parseDecLong(), owner = userInfo.id).asFlow().collect {
                        //Send the converted json.
                        send(Frame.Text(gson.toJson(it.executeAsOne()).toString().clean()))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("race/getLiveRaceSubraceNameIds") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.raceSubraceCrossRefQueries.selectSubraceNameIdForRace(
                        raceId = receivedText.toLong(),
                        owner = userInfo.id
                    ).asFlow().collect {
                        //Send the converted json.
                        send(Frame.Text(gson.toJson(it.executeAsList()).toString().clean()))
                    }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("race/getAllRaceNameIds") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                db.racesQueries.selectNameIdFor(
                    owner = userInfo.id
                ).asFlow().collect {
                    //Send the converted json.
                    send(Frame.Text(gson.toJson(it.executeAsList()).toString().clean()))
                }
            }
        }
    }
}