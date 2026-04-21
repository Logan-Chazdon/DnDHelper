package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.put
import model.Feature
import model.Subrace
import model.SubraceEntity

class SubraceService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        InsertSubraceFeature("$PATH/insertSubraceFeature"),
        LiveSubrace("$PATH/getLiveSubrace"),
        RemoveSubraceFeature("$PATH/removeSubraceFeature"),
        InsertSubrace("$PATH/insertSubrace"),
        RemoveRaceSubrace("$PATH/removeRaceSubrace"),
        LiveHomebrewSubraces("$PATH/liveHomebrewSubraces"),
        DeleteSubrace("$PATH/deleteSubrace"),
        LiveFilledSubraces("$PATH/liveFilledSubraces"),
        LiveSubraceFeatures("$PATH/liveSubraceFeatures"),
    }

    companion object {
        const val PATH = "subrace"
    }


    suspend fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        postTo(Paths.InsertSubraceFeature.path) {
            put("subraceId", subraceId)
            put("featureId", featureId)
        }
    }

    fun getSubrace(id: Int): Flow<Subrace> {
        return flow {
            client.wss(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveSubrace.path) {
                while (true) {
                    send(Frame.Text(id.toString()))
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "received") {
                        val item = format.decodeFromString<Subrace>(othersMessage!!.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    suspend fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        deleteFrom(Paths.RemoveSubraceFeature.path) {
            append("subraceId", subraceId.toString())
            append("featureId", featureId.toString())
        }
    }

    suspend fun insertSubrace(subrace: SubraceEntity): Int {
        val id = client.post {
            url {
                protocol= URLProtocol.HTTPS
                host = apiUrl
                port = targetPort
                path(Paths.InsertSubrace.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(subrace))
        }.bodyAsText()
        return id.toInt()
    }

    /**Fetch all subraces for a race with featOptions and features filled.*/
    fun bindSubraceOptions(raceId: Int): Flow<MutableList<Subrace>> {
        return flow {
            client.wss(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveFilledSubraces.path) {
                //Send id
                send(Frame.Text(raceId.toString()))
                val othersMessage = incoming.receive() as? Frame.Text
                val item = format.decodeFromString<MutableList<Subrace>>(othersMessage!!.readText())
                emit(item)
            }
        }
    }

    suspend fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        deleteFrom(Paths.RemoveRaceSubrace.path) {
            append("raceId", raceId.toString())
            append("subraceId", subraceId.toString())
        }
    }

    fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        return flow {
            client.wss(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveHomebrewSubraces.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "received") {
                        val item = format.decodeFromString<List<SubraceEntity>>(othersMessage!!.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return flow {
            client.wss(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveSubraceFeatures.path) {
                while (true) {
                    send(id.toString())
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val list = format.decodeFromString<List<Feature>>(othersMessage!!.readText())
                        emit(list)
                    }
                }
            }
        }
    }

    suspend fun deleteSubrace(id: Int) {
        deleteFrom(Paths.DeleteSubrace.path) {
            append("id", id.toString())
        }
    }
}