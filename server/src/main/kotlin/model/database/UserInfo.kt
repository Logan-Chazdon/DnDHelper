package gmail.loganchazdon.dndhelper.model.database

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject


@Serializable
data class UserInfo(
    val id: String,
    @SerialName("given_name")
    val name: String,
)

private val json = Json { ignoreUnknownKeys = true }

internal suspend fun request(httpClient: HttpClient, token: String): HttpResponse {
    return httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

suspend fun getUserInfo(
    httpClient: HttpClient,
    userSession: UserSession,
    call : ApplicationCall
): UserInfo {
    return try {
        json.decodeFromString(request(httpClient, userSession.accessToken).bodyAsText())
    } catch (e: Exception) {
        val response = httpClient.post("https://oauth2.googleapis.com/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(listOf(
                "client_id" to System.getenv("clientId"),
                "client_secret" to System.getenv("clientSecret"),
                "refresh_token" to userSession.refreshToken,
                "grant_type" to "refresh_token"
            ).formUrlEncode())
        }
        val jsonObject = JSONObject(response.bodyAsText())
        try {
            call.sessions.set(
                UserSession(
                    accessToken = jsonObject.getString("access_token"),
                    refreshToken = userSession.refreshToken
                )
            )
        } catch(e: TooLateSessionSetException) {
            //TODO consider removing this for prod
            //Its only here to allow for easier testing of websockets.
        }
        val new = request(httpClient, jsonObject.getString("access_token")).bodyAsText()
        json.decodeFromString(new)
    }
}

