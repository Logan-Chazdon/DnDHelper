package services

import io.ktor.client.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import model.*


abstract class Service(
    val apiUrl: String = "localhost",   //TODO replace this with automatic dev / prod api url
    val targetPort: Int = 8080,
    val client : HttpClient
) {
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
    val format = Json { serializersModule = module; ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false; }
}