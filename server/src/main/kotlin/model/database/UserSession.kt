package gmail.loganchazdon.dndhelper.model.database

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val accessToken: String, val refreshToken: String?)

suspend fun getSession(
    call: ApplicationCall
): UserSession? {
    val userSession: UserSession? = call.sessions.get()
    //if there is no session, redirect to login
    if (userSession == null) {
        val redirectUrl = URLBuilder("http://0.0.0.0:8080/login").run {
            parameters.append("redirectUrl", call.request.uri)
            build()
        }
        call.respondRedirect(redirectUrl)
        return null
    }
    return userSession
}