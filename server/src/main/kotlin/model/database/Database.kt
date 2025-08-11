package gmail.loganchazdon.dndhelper.model.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.auth0.jwt.JWT
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gmail.loganchazdon.database.*
import gmail.loganchazdon.dndhelper.model.dataSources.ServerDataSource
import gmail.loganchazdon.dndhelper.model.services.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject


val gsonInstance = GsonBuilder()
    .disableHtmlEscaping()
    .create()
val Routing.gson: Gson
    get() = gsonInstance

//Used to unquote json stored as strings.
private val regex = "\"(\\{.*?\\}|\\[.*?\\])\"".toRegex()
fun String.clean(): String {
    return replace(regex, "$1").replace("\\", "").replace("\"null\"", "null")
}

val applicationHttpClient = HttpClient {

}

suspend fun RoutingContext.withUserInfo(
    autoRespond : Boolean= true,
    block: suspend RoutingContext.(userInfo: UserInfo) -> Unit
) {
    getSession(call)?.let { session ->
        val userInfo = getUserInfo(applicationHttpClient, session, call)
        block(userInfo)
        if(autoRespond) call.respond(HttpStatusCode.OK)
    }
}


fun Application.configureDatabases() {
    install(Sessions) {
        //TODO Prod change sessionStorage
        //TODO Prod look at encryption
        cookie<UserSession>("USER_SESSION" /*,directorySessionStorage(File("build/.sessions"))*/) {

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
            spellCastingAdapter = jsonObjectAdapter,
            pactMagicAdapter = jsonObjectAdapter
        ),
        backgroundsAdapter = Backgrounds.Adapter(
            proficienciesAdapter = jsonListAdapter,
            proficiencyChoicesAdapter = jsonListAdapter,
            languagesAdapter = jsonListAdapter,
            languageChoicesAdapter = jsonListAdapter,
            equipmentAdapter = jsonListAdapter,
            equipmentChoicesAdapter = jsonListAdapter
        ),
        spellsAdapter = Spells.Adapter(
            componentsAdapter = jsonListAdapter,
            itemComponentsAdapter = jsonListAdapter,
            classesAdapter = jsonListAdapter
        ),
        subracesAdapter = Subraces.Adapter(
            languagesAdapter = jsonListAdapter,
            languageChoicesAdapter = jsonListAdapter,
            abilityBonusesAdapter = jsonListAdapter,
            abilityBonusChoiceAdapter = jsonObjectAdapter,
            startingProficienciesAdapter = jsonListAdapter
        ),
        featuresAdapter = Features.Adapter(
            activationRequirementAdapter = jsonObjectAdapter,
            maxActiveAdapter = jsonObjectAdapter,
            prerequisiteAdapter = jsonObjectAdapter,
            acAdapter = jsonObjectAdapter,
            speedBoostAdapter = jsonObjectAdapter,
            infusionAdapter = jsonObjectAdapter,
            proficienciesAdapter = jsonListAdapter,
            expertisesAdapter = jsonListAdapter,
            languagesAdapter = jsonListAdapter
        ),
        racesAdapter = Races.Adapter(
            startingProficienciesAdapter = jsonListAdapter,
            proficiencyChoicesAdapter = jsonListAdapter,
            languagesAdapter = jsonListAdapter,
            languageChoicesAdapter = jsonListAdapter,
            abilityBonusesAdapter = jsonListAdapter,
        ),
        FeatureChoiceEntityAdapter = FeatureChoiceEntity.Adapter(
            chooseAdapter = jsonObjectAdapter
        ),
        ClassChoiceEntityAdapter = ClassChoiceEntity.Adapter(
            abilityImprovementsGrantedAdapter = jsonListAdapter,
            proficiencyChoicesByStringAdapter = jsonListAdapter,
        ),
        featsAdapter = Feats.Adapter(
            abilityBonusesAdapter = jsonListAdapter,
            abilityBonusChoiceAdapter = jsonObjectAdapter,
            prerequisiteAdapter = jsonObjectAdapter,
        ),
        subclassesAdapter = Subclasses.Adapter(
            subclass_spell_castingAdapter = jsonObjectAdapter
        ),
        BackgroundChoiceEntityAdapter = BackgroundChoiceEntity.Adapter(
            languageChoicesAdapter = jsonListAdapter
        ),
        RaceChoiceEntityAdapter = RaceChoiceEntity.Adapter(
            proficiencyChoiceAdapter = jsonListAdapter,
            languageChoiceAdapter = jsonListAdapter,
            abilityBonusOverridesAdapter = jsonListAdapter
        ),
        SubraceChoiceEntityAdapter = SubraceChoiceEntity.Adapter(
            abilityBonusOverridesAdapter = jsonListAdapter
        )
    )


    val dataSource = ServerDataSource(db)

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

        backgroundService(db, applicationHttpClient)
        characterService(db, applicationHttpClient)
        classService(db, applicationHttpClient)
        featService(db, applicationHttpClient)
        featureService(db, applicationHttpClient)
        raceService(db, applicationHttpClient)
        subraceService(db, applicationHttpClient)
        subclassService(db, applicationHttpClient)
        spellService(db, applicationHttpClient)
        pullSyncService(db)


        dataSourceService(dataSource, applicationHttpClient)


        post("/session") {
            val authCode = call.receiveText()

            val response = applicationHttpClient.post("https://oauth2.googleapis.com/token") {
                setBody(buildJsonObject {
                    put("code", authCode)
                    put("client_id", System.getenv("webClientId"))
                    put("client_secret", System.getenv("webClientSecret"))
                    put("grant_type", "authorization_code")
                }.toString())
            }.bodyAsText()

            val tokens = JSONObject(response)

            call.sessions.set(
                name = "USER_SESSION",
                value = UserSession(
                    accessToken = tokens.getString("access_token"),
                    refreshToken = tokens.optString("refresh_token")
                )
            )

            val verifier = GoogleIdTokenVerifier.Builder(
                ApacheHttpTransport(),
                GsonFactory()
            ).setAudience(listOf("257942461839-fta2f7lbg6tcmuvm0ofq5fkct2d3ql5g.apps.googleusercontent.com"))
                .build()


            val idToken: GoogleIdToken = verifier.verify(tokens.getString("id_token"))
            val payload = idToken.payload


            db.usersQueries.insertUsers(
                id = payload.subject,
                name = payload["name"] as String
            )

            call.respond(HttpStatusCode.OK)
        }


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

