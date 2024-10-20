package gmail.loganchazdon.dndhelper.model.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import gmail.loganchazdon.database.Characters
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.services.characterService
import gmail.loganchazdon.dndhelper.model.services.classService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

private val gsonInstance = GsonBuilder()
    .setPrettyPrinting()
    .create()
val Routing.gson: Gson
    get() = gsonInstance
//Used to unquote json stored as strings.
private val regex = "\"(\\{.*?\\}|\\[.*?\\])\"".toRegex()
fun String.clean() : String {
    return replace(regex, "$1").replace("\\", "").replace("\"null\"", "null")
}

fun Application.configureDatabases() {


    install(WebSockets) {
        pingPeriodMillis = 15 * 1000
        timeoutMillis = 15 * 1000
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val stringListAdapter = object : ColumnAdapter<List<String>, String> {
        override fun decode(databaseValue: String): List<String> {
            return gsonInstance.fromJson(databaseValue, object : TypeToken<List<String>>() {}.type)
        }

        override fun encode(value: List<String>): String {
            return gsonInstance.toJson(value)
        }
    }

    val jsonListAdapter = object : ColumnAdapter<JsonArray, String> {
        override fun decode(databaseValue: String): JsonArray {
            return gsonInstance.fromJson(databaseValue, object : TypeToken<JsonArray>() {}.type)
        }

        override fun encode(value: JsonArray): String {
            return gsonInstance.toJson(value)
        }
    }

    val jsonObjectAdapter = object : ColumnAdapter<JsonObject, String> {
        override fun decode(databaseValue: String): JsonObject {
            return gsonInstance.fromJson(databaseValue, object : TypeToken<JsonObject>() {}.type)
        }

        override fun encode(value: JsonObject): String {
            return gsonInstance.toJson(value)
        }
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
            addedProficienciesAdapter =jsonListAdapter
        ),
    )

    routing {
        characterService(db)
        classService(db)
    }
}

