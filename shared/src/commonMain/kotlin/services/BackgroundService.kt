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
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity

class BackgroundService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        AllBackgrounds("$PATH/allBackgrounds"),
        InsertBackground("$PATH/insertBackground"),
        InsertBackgroundSpell("$PATH/insertBackgroundSpell"),
        BackgroundSpells("$PATH/backgroundSpells"),
        DeleteBackground("$PATH/deleteBackground"),
        BackgroundFeatures("$PATH/backgroundFeatures"),
        HomebrewBackgrounds("$PATH/homebrewBackgrounds"),
        BackgroundChoiceData("$PATH/backgroundChoiceData"),
        BackgroundEntity("$PATH/backgroundEntity"),
        InsertBackgroundFeature("$PATH/insertBackgroundFeature"),
    }

    companion object {
        const val PATH = "background"
    }

    fun getAllBackgrounds(): Flow<List<Background>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.AllBackgrounds.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<BackgroundEntity>>(response).map {
                Background(
                    it,
                    mutableListOf()
                )
            }
            emit(list)
        }
    }

    suspend fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertBackground.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(backgroundEntity))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
        postTo(Paths.InsertBackgroundSpell.path) {
            put("backgroundId", backgroundId.toString())
            put("spellId", spellId.toString())
        }
    }

    suspend fun getBackgroundSpells(backgroundId: Int): List<Spell>? {
        return format.decodeFromString(getFrom(Paths.BackgroundSpells.path) {
            append("backgroundId", backgroundId.toString())
        }.bodyAsText())
    }

    suspend fun deleteBackground(id: Int) {
        deleteFrom(Paths.DeleteBackground.path) {
            append("id", id.toString())
        }
    }

    suspend fun getBackgroundFeatures(id: Int): List<Feature> {
        return format.decodeFromString(
            getFrom(Paths.BackgroundFeatures.path) {
                append("id", id.toString())
            }.bodyAsText()
        )
    }

    suspend fun getUnfilledBackgroundFeatures(id: Int): List<Feature> {
        return format.decodeFromString(
            getFrom(Paths.BackgroundFeatures.path) {
                append("id", id.toString())
            }.bodyAsText()
        )
    }

    fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.HomebrewBackgrounds.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = format.decodeFromString<List<BackgroundEntity>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }


    suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return format.decodeFromString(getFrom(Paths.BackgroundChoiceData.path) {
            append("id", charId.toString())
        }.bodyAsText())
    }

    fun getUnfilledBackground(id: Int): Flow<BackgroundEntity> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.BackgroundEntity.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as Frame.Text
                    if (othersMessage.readText() != "Invalid Id") {
                        val item = format.decodeFromString<BackgroundEntity>(othersMessage.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    suspend fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
        postTo(Paths.InsertBackgroundFeature.path) {
            put("backgroundId", backgroundId)
            put("featureId", featureId)
        }
    }

}