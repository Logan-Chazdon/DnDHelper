package services

import io.ktor.client.*


abstract class Service(
    val apiUrl: String = "localhost",   //TODO replace this with automatic dev / prod api url
    val targetPort: Int = 8080,
    val client : HttpClient
) {
}