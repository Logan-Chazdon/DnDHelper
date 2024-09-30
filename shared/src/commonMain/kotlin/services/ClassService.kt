package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.Character


class ClassService(client: HttpClient) : Service(client = client) {
    fun getAllClasses() : Flow<List<Character>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.AllClasses.path) {
                while(true) {
                    //Note this is not filling the character.
                    emit(receiveDeserialized<List<Character>>())
                }
            }
        }
    }

    enum class Paths(val path: String) {
        AllClasses("class/allClasses"),
    }
}

