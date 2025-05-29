package services

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import model.*


abstract class Service(
    val apiUrl: String = "localhost",   //TODO replace this with automatic dev / prod api url
    val targetPort: Int = 8080,
    val client : HttpClient
) {
    protected suspend fun postTo(path: String, json:  JsonObjectBuilder.() -> Unit) {
        client.post {
            url {
                host = apiUrl
                port = targetPort
                path(path)
            }
            setBody(buildJsonObject(json).toString())
        }
    }

    protected suspend fun deleteFrom(path: String, params : ParametersBuilder.() -> Unit) {
        client.delete {
            url {
                host = apiUrl
                port = targetPort
                path(path)
                parameters.params()
            }
        }
    }

    protected suspend fun getFrom(path: String, params :  ParametersBuilder.() -> Unit ): HttpResponse {
        return client.get {
            url {
                host = apiUrl
                port = targetPort
                path(path)
                parameters.params()
            }
        }
    }


    private val module = SerializersModule {
        polymorphic(
            baseClass = ItemInterface::class,
            actualClass = Item::class,
            actualSerializer = serializer(),
        )
        polymorphic(
            baseClass = ItemInterface::class,
            actualClass = Weapon::class,
            actualSerializer = serializer(),
        )
        polymorphic(
            baseClass = ItemInterface::class,
            actualClass = Shield::class,
            actualSerializer = serializer(),
        )
        polymorphic(
            baseClass = ItemInterface::class,
            actualClass = Currency::class,
            actualSerializer = serializer(),
        )
        polymorphic(
            baseClass = ItemInterface::class,
            actualClass = Armor::class,
            actualSerializer = serializer(),
        )
    }
    val format = Json { serializersModule = module; ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false; allowStructuredMapKeys = true}


}