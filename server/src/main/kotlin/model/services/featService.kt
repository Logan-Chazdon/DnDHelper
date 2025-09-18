package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.coroutines.asFlow
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.getSession
import gmail.loganchazdon.dndhelper.model.database.getUserInfo
import gmail.loganchazdon.dndhelper.model.database.gson
import gmail.loganchazdon.dndhelper.model.database.utils.fillOutFeatureListWithoutChosen
import gmail.loganchazdon.dndhelper.model.database.withUserInfo
import io.ktor.client.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Routing.featService(db: Database, httpClient: HttpClient) {
    webSocket("feat/unfilledFeats") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.featsQueries.selectAllFor(userInfo.id).asFlow().collect { x ->
                send(gson.toJson(x.executeAsList()))
            }
        }
    }

    get("feat/featFeatures") {
        withUserInfo { userInfo ->
            val features = db.featFeatureCrossRefQueries.selectFeaturesForFeat(
                owner = userInfo.id,
                featId = call.parameters["id"]!!.toLong()
            ).executeAsList()
            call.respond(db.fillOutFeatureListWithoutChosen(features, userInfo.id).toString())
        }
    }
}