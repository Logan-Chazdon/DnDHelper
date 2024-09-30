package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import model.Character
import model.CharacterEntity


class CharacterService(client: HttpClient) : Service(client = client) {
    /**This method emits mostly empty character objects for the main character list display do not count on the rest of the data.
     */
    fun getAllCharacters(): Flow<List<Character>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.AllCharacters.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    val myMessage = "test"
                    if (myMessage != null) {
                        send(myMessage)
                    }
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = Json.decodeFromString<List<CharacterEntity>>(othersMessage!!.readText()).map {
                            Character(it.name, id = it.id)
                        }
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    suspend fun postCharacter(character: CharacterEntity): Long {
        return client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.PostCharacter.path)
            }
            contentType(ContentType.Application.Json)
            setBody(character)
        }.bodyAsText().toLong()
    }

    suspend fun deleteCharacter(id: Int) {
        client.delete {
            url {
                host = apiUrl
                port = targetPort
                path("${Paths.DeleteCharacter.path}/$id")
            }
        }
    }


    enum class Paths(val path: String) {
        AllCharacters("$PATH/getLiveCharacters"),
        PostCharacter("$PATH/postCharacter"),
        DeleteCharacter("$PATH/deleteCharacter"),
    }

    companion object {
        const val PATH = "character"
    }
}