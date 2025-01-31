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
import model.Spell
import model.pojos.NameAndIdPojo
import services.CharacterService.Paths

class SpellService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        InsertSpell("$PATH/insertSpell"),
        RemoveClassSpell("$PATH/removeClassSpell"),
        AddClassSpell("$PATH/addClassSpell"),
        AllSpellsLive("$PATH/allSpellsLive"),
        HomebrewSpellsLive("$PATH/homebrewSpellsLive"),
        LiveSpell("$PATH/liveSpell"),
        LiveSpellClasses("$PATH/liveSpellClasses"),
        DeleteSpell("$PATH/deleteSpell"),
    }

    companion object {
        const val PATH = "spell"
    }


    suspend fun insertSpell(spell: Spell): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertSpell.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(spell))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun removeClassSpellCrossRef(classId: Int, spellId: Int) {
        deleteFrom(Paths.RemoveClassSpell.path) {
            append("classId", classId.toString())
            append("spellId", spellId.toString())
        }
    }

    suspend fun addClassSpellCrossRef(classId: Int, spellId: Int) {
        postTo(Paths.AddClassSpell.path) {
            put("classId", classId)
            put("spellId", spellId)
        }
    }

    fun getAllSpells(): Flow<List<Spell>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.AllSpellsLive.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = format.decodeFromString<List<Spell>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    fun getHomebrewSpells(): Flow<List<Spell>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.HomebrewSpellsLive.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = format.decodeFromString<List<Spell>>(othersMessage!!.readText())
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    fun getLiveSpell(id: Int): Flow<Spell> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveSpell.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val item = format.decodeFromString<Spell>(othersMessage!!.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveSpellClasses.path) {
                send(id.toString())
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    if (othersMessage?.readText() != "received") {
                        val item = format.decodeFromString<List<NameAndIdPojo>>(othersMessage!!.readText())
                        emit(item)
                    }
                }
            }
        }
    }

    suspend fun removeSpellById(id: Int) {
        deleteFrom(Paths.DeleteSpell.path) {
            append("id", id.toString())
        }
    }
}