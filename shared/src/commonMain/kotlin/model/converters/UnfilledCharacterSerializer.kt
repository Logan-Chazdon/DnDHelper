package model.converters

import kotlinx.serialization.json.*
import model.Character
import model.Language
import model.Proficiency
import model.Resource

object UnfilledCharacterSerializer {
    fun deserialize(input: String): Character {
        val json = Json.parseToJsonElement(input).jsonObject
        fun getString(key: String): String {
            println(key)
            return json[key]?.jsonPrimitive?.content ?: ""
        }

        fun getInt(key: String): Int {
            println(key)
            return json[key]?.jsonPrimitive?.int ?: 0
        }

        fun getBool(key: String): Boolean {
            println(key)
            return json[key]?.jsonPrimitive?.boolean ?: false
        }

        fun getStringList(key: String): MutableList<String> {
            println(key)
            return (json[key]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()).toMutableList()
        }

        fun getArray(key: String): JsonArray? {
            println(key)
            return json[key]?.jsonArray
        }

        fun getProfs(key: String): MutableList<Proficiency> {
            println(key)
            return getArray(key)?.map { Json.decodeFromJsonElement<Proficiency>(it) }?.toMutableList()
                ?: mutableListOf()
        }

        fun getLangs(key: String): MutableList<Language> {
            return getArray(key)?.map { Json.decodeFromJsonElement<Language>(it) }?.toMutableList()
                ?: mutableListOf()
        }

        fun getSlots(key: String): MutableList<Resource> {
            return getArray(key)?.map { Json.decodeFromJsonElement<Resource>(it) }?.toMutableList()
                ?: mutableListOf()
        }

        fun getJsonObject(key: String) : String {
            println(key)
            return json[key]?.jsonObject.toString()
        }

        try {
            return Character(
                name = getString("name"),
                personalityTraits = getString("personalityTraits"),
                ideals = getString("ideals"),
                bonds = getString("bonds"),
                flaws = getString("flaws"),
                notes = getString("notes"),
                currentHp = getInt("currentHp"),
                tempHp = getInt("tempHp"),
                conditions = getStringList("conditions"),
                resistances = getStringList("resistances"),
                id = getInt("id"),
                statGenerationMethodIndex = getInt("statGenerationMethodIndex"),
                baseStats = Json.decodeFromString(getJsonObject("baseStats")),
                backpack = Json.decodeFromString(getJsonObject("backpack")),
                inspiration = getBool("inspiration"),
                positiveDeathSaves = getInt("positiveDeathSaves"),
                negativeDeathSaves = getInt("negativeDeathSaves"),
                spellSlots = getSlots("spellSlots"),
                addedLanguages = getLangs("addedLanguages"),
                addedProficiencies = getProfs("addedProficiencies")
            ).apply {
                println(json.keys)
                race =  if(json.keys.contains("race")) {
                    Json.decodeFromString(getString("race"))
                } else null

                background = if(json.keys.contains("background"))
                    Json.decodeFromString(getString("background"))
                else null
            }
        }catch(e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}