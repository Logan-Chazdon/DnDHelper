package gmail.loganchazdon.dndhelper.model.database

import app.cash.sqldelight.ColumnAdapter
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

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