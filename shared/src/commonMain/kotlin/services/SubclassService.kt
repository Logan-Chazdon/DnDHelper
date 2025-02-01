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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put
import model.Feature
import model.Subclass
import model.SubclassEntity

class SubclassService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        SubclassesById(path = "$PATH/subclassesById"),
        InsertSubclass(path = "$PATH/insertSubclass"),
        LiveSubclass(path = "$PATH/liveSubclass"),
        RemoveSubclassFeature(path = "$PATH/removeSubclassFeature"),
        InsertSubclassFeature(path = "$PATH/insertSubclassFeature"),
        HomebrewSubclasses(path = "$PATH/homebrewSubclasses"),
        SubclassFeatures(path = "$PATH/subclassFeatures"),
        SubclassLiveFeatures(path = "$PATH/subclassLiveFeatures"),
        DeleteSubclass(path = "$PATH/deleteSubclass"),
    }

    companion object {
        const val PATH = "subclass"
    }

    fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.SubclassesById.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "Invalid Id") {
                        val listToEmit = Json.decodeFromString<List<Subclass>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    suspend fun insertSubclass(subClass: SubclassEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertSubclass.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(subClass))
        }.bodyAsText()
        return id.toInt()
    }

    fun getSubclass(id: Int): Flow<Subclass> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveSubclass.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "Invalid Id") {
                        val item = format.decodeFromString<Subclass>(othersMessage!!.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    suspend fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        deleteFrom(Paths.RemoveSubclassFeature.path) {
            append("subclassId", subclassId.toString())
            append("featureId", featureId.toString())
        }
    }

    suspend fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        postTo(Paths.InsertSubclassFeature.path) {
            put("subclassId", subclassId)
            put("featureId", featureId)
        }
    }

    fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.HomebrewSubclasses.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    val listToEmit = format.decodeFromString<List<Subclass>>(othersMessage!!.readText())
                    emit(listToEmit)
                }
            }
        }
    }

    suspend fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature> {
        return format.decodeFromString(getFrom(Paths.SubclassFeatures.path) {
            append("subclassId", subclassId.toString())
            append("maxLevel", maxLevel.toString())
        }.bodyAsText())
    }

    fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.SubclassLiveFeatures.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "Invalid Id") {
                        val listToEmit = Json.decodeFromString<List<Feature>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    suspend fun deleteSubclass(subclassId: Int) {
        deleteFrom(Paths.DeleteSubclass.path) {
            append("subclassId", subclassId.toString())
        }
    }
}