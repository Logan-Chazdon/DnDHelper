package gmail.loganchazdon.dndhelper.model.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.auth0.jwt.JWT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gmail.loganchazdon.database.Characters
import gmail.loganchazdon.database.Classes
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.services.characterService
import gmail.loganchazdon.dndhelper.model.services.classService
import gmail.loganchazdon.dndhelper.model.services.raceService
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*

val gsonInstance = GsonBuilder()
    .setPrettyPrinting()
    .create()
val Routing.gson: Gson
    get() = gsonInstance
//Used to unquote json stored as strings.
private val regex = "\"(\\{.*?\\}|\\[.*?\\])\"".toRegex()
fun String.clean() : String {
    return replace(regex, "$1").replace("\\", "").replace("\"null\"", "null")
}

val applicationHttpClient = HttpClient {

}

suspend fun RoutingContext.withUserInfo(block: suspend  RoutingContext.(userInfo: UserInfo) -> Unit) {
    getSession(call)?.let { session ->
        val userInfo = getUserInfo(applicationHttpClient, session, call)
        block(userInfo)
    }
}


fun Application.configureDatabases() {
    install(Sessions) {
        //TODO Prod change sessionStorage
        //TODO Prod look at encryption
        cookie<UserSession>("USER_SESSION", /*,directorySessionStorage(File("build/.sessions"))*/) {

        }
    }
    install(Authentication) {

        oauth("auth-oauth-google") {
            client = HttpClient(Apache)
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://oauth2.googleapis.com/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("clientId"),
                    clientSecret = System.getenv("clientSecret"),
                    defaultScopes = listOf("profile", "email", "openid"),
                    extraAuthParameters = listOf(Pair("access_type", "offline"))
                )
            }
            urlProvider = { "http://localhost:8080/callback" }
        }
    }
    install(WebSockets) {
        pingPeriodMillis = 15 * 1000
        timeoutMillis = 15 * 1000
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
    Database.Schema.create(driver)
    val db = Database(
        driver,
        charactersAdapter = Characters.Adapter(
            conditionsAdapter = jsonListAdapter,
            resistancesAdapter = jsonListAdapter,
            baseStatsAdapter = jsonObjectAdapter,
            backpackAdapter = jsonObjectAdapter,
            spellSlotsAdapter = jsonListAdapter,
            addedLanguagesAdapter = jsonListAdapter,
            addedProficienciesAdapter = jsonListAdapter
        ),
        classesAdapter = Classes.Adapter(
            proficiencyChoicesAdapter = jsonListAdapter,
            proficienciesAdapter = jsonListAdapter,
            equipmentChoicesAdapter = jsonListAdapter,
            equipmentAdapter = jsonListAdapter,
            //spellCastingAdapter = jsonObjectAdapter,
            //pactMagicAdapter = jsonObjectAdapter
        ),
    )

    routing {
        authenticate("auth-oauth-google") {
            get("/login") {

            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                if (principal != null) {
                    call.sessions.set(UserSession(principal.accessToken, principal.refreshToken))
                    val claims = JWT().decodeJwt(principal.extraParameters["id_token"]).claims

                    val id = claims["sub"]?.asString()
                    val name = claims["given_name"]?.asString()
                    id?.let { db.usersQueries.insertUsers(id, name) }
                }

                //We redirect to the same location because our app uses jetpack navigation and not separate web pages.
                //Change if needed.
                call.respondRedirect("http://localhost:8081")
            }

        }

        characterService(db, applicationHttpClient)
        classService(db, applicationHttpClient)
        raceService(db, applicationHttpClient)

        get("/") {
            val userSession: UserSession? = call.sessions.get()
            if (userSession != null) {
                //Logged in
                call.respondText("1")
            } else {
                //Failed
                call.respondText("0")
            }
        }
    }
}

