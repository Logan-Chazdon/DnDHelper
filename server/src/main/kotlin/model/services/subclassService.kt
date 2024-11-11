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
import org.json.JSONObject

fun Routing.subclassService(db: Database, httpClient: HttpClient) {

    post("subclass/insertSubclass") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val subclass = gson.fromJson(response, Subclasses::class.java)
            db.subclassesQueries.insert(subclass.copy(owner = userInfo.id))
            call.respondText(subclass.subclassId.toString())
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
            val body = JSONObject(call.receiveText())
            val query = db.subclassFeatureCrossRefQueries.selectFeaturesBySubclass(
                owner = userInfo.id,
                subclassId = body.getLong("subclassId")
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
                    db.subclassesQueries.selectByClass(classId = receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val subclasses = gson.toJson(it)

                        //Send the converted json.
                        send(Frame.Text(subclasses))
                    }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("subclass/subclassesById") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.subclassesQueries.selectByClass(classId = receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val subclasses = gson.toJson(it)

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
                        subclassId = receivedText.toLong()
                    ).asFlow().collect {
                        val features = gson.toJson(it)

                        //Send the converted json.
                        send(Frame.Text(features))
                    }
                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    webSocket("subclass/subclassesById") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.subclassesQueries.selectHomebrewSubclasses(owner = userInfo.id).asFlow().collect {
                val subclasses = gson.toJson(it)
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
                    db.subclassesQueries.selectSubclass(id = receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val subclass = gson.toJson(it)

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