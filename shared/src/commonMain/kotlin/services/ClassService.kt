package services

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.Class
import model.ClassEntity
import services.CharacterService.Paths


class ClassService(client: HttpClient) : Service(client = client) {
    fun getAllClasses() : Flow<List<Class>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.AllClasses.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<ClassEntity>>(response).map {
                Class(
                    it,
                    mutableListOf()
                )
            }
            emit(list)
        }
    }

    /**Not currently configured to provide database updates. Flow is only used to allow for ui rendering.
    */
    fun getUnfilledClass(id: Int): Flow<ClassEntity> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path("${Paths.UnfilledClass.path}/$id")
                }
            }.call.response.bodyAsText()
            emit(format.decodeFromString<ClassEntity>(response))
        }
    }

    enum class Paths(val path: String) {
        AllClasses("class/getAllClasses"),
        UnfilledClass("class/getUnfilledClass"),
    }
}

