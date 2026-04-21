package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import model.Feat
import model.Feature

class FeatService(client: HttpClient) : Service(client = client) {

    enum class Paths(val path: String) {
        UnfilledFeats("$PATH/unfilledFeats"),
        FeatFeatures("$PATH/featFeatures"),
    }

    companion object {
        const val PATH = "feat"
    }


    fun getUnfilledFeats(): Flow<List<Feat>> {
        return flow {
            client.wss(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.UnfilledFeats.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = Json.decodeFromString<List<Feat>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    suspend fun getFeatFeatures(featId: Int): List<Feature> {
        return format.decodeFromString(getFrom(Paths.FeatFeatures.path) {
            append("id", featId.toString())
        }.bodyAsText())
    }
}