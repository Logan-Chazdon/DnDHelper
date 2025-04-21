package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import gmail.loganchazdon.database.*
import gmail.loganchazdon.dndhelper.model.database.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

private val arrayConverter = fun(array: JsonArray) : JSONArray {
    return JSONArray(array.toString())
}
private val objectConverter = fun(obj: JsonObject) : JSONObject {
    return JSONObject(obj.toString())
}

private fun serializeUnfilledCharacter(sqlResponse: CharacterView): JSONObject {
    val character = JSONObject()

    //Add character data.
    character.put("id", sqlResponse.id)
    character.put("name", sqlResponse.name)
    character.put("bonds", sqlResponse.bonds)
    character.put("flaws", sqlResponse.flaws)
    character.put("notes", sqlResponse.notes)
    character.put("personalityTraits", sqlResponse.personalityTraits)
    character.put("ideals", sqlResponse.ideals)
    character.put("currentHp", sqlResponse.currentHp)
    character.put("tempHp", sqlResponse.tempHp)
    character.put("conditions", arrayConverter(sqlResponse.conditions))
    character.put("resistances", arrayConverter(sqlResponse.resistances))
    character.put("statGenerationMethodIndex", sqlResponse.statGenerationMethodIndex)
    character.put("baseStats", objectConverter(sqlResponse.baseStats))
    character.put("backpack", objectConverter(sqlResponse.backpack))
    character.put("inspiration", sqlResponse.inspiration)
    character.put("positiveDeathSaves", sqlResponse.positiveDeathSaves)
    character.put("negativeDeathSaves", sqlResponse.negativeDeathSaves)
    character.put("spellSlots", arrayConverter(sqlResponse.spellSlots))
    character.put("addedLanguages", arrayConverter(sqlResponse.addedLanguages))
    character.put("addedProficiencies", arrayConverter(sqlResponse.addedProficiencies))

    //Add Race data
    val raceId = sqlResponse.raceId
    if (raceId != null) {
        val race = JSONObject()
        val subrace = JSONObject()
        race.apply {
            put("id", raceId)
            put("name", sqlResponse.raceName)
            put("groundSpeed", sqlResponse.groundSpeed)
            put("abilityBonuses", sqlResponse.abilityBonuses)
            put("alignment", sqlResponse.alignment)
            put("age", sqlResponse.age)
            put("size", sqlResponse.size)
            put("sizeDesc", sqlResponse.sizeDesc)
            put("startingProficiencies", sqlResponse.startingProficiencies)
            put("proficiencyChoices", sqlResponse.proficiencyChoices)
            put("languages", sqlResponse.languages)
            put("languageDesc", sqlResponse.languageDesc)
            put("isHomebrew", sqlResponse.isHomebrew)
            put("abcchoose", sqlResponse.abcchoose)
            put("abcfrom", sqlResponse.abcfrom)
            put("abcmaxOccurrencesOfAbility", sqlResponse.abcmaxOccurrencesOfAbility)
            put("abcchosenByString", sqlResponse.abcchosenByString)
        }
        subrace.apply {
            put("id", sqlResponse.subraceid)
            put("name", sqlResponse.subracename)
            put("abilityBonuses", sqlResponse.subraceabilityBonuses)
            put("abilityBonusChoice", sqlResponse.subraceabilityBonusChoice)
            put("startingProficiencies", sqlResponse.subracestartingProficiencies)
            put("languages", sqlResponse.subracelanguages)
            put("languageChoices", sqlResponse.subracelanguageChoices)
            put("size", sqlResponse.subracesize)
        }

        race.put("subrace", subrace)
        character.put("race", race)
    }

    val backgroundId = sqlResponse.backgroundid
    if (backgroundId != null) {
        //Add background data
        val background = JSONObject()
        background.apply {
            put("id", backgroundId)
            put("name", sqlResponse.backgroundname)
            put("desc", sqlResponse.backgrounddesc)
            put("spells", sqlResponse.backgroundspells)
            put("proficiencies", sqlResponse.backgroundproficiencies)
            put("languages", sqlResponse.backgroundlanguages)
            put("equipment", sqlResponse.backgroundequipment)
            put("equipmentChoices", sqlResponse.backgroundequipmentChoices)
        }
        character.put("background", background)
    }
    return character
}

private fun serializeCharacterClasses(query: Query<SelectClasses>) : JSONObject {
    val classes = JSONObject()
    val sqlResponse = query.executeAsList()

    sqlResponse.forEach { data ->
        val clazz = JSONObject()


        clazz.put("name", data.name)
        clazz.put("id", data.classId)
        clazz.put("isBaseClass", data.isBaseClass)
        clazz.put("abilityImprovementsGranted", JSONArray(data.abilityImprovementsGranted))
        clazz.put("equipment", arrayConverter(data.equipment))
        clazz.put("equipmentChoices", arrayConverter(data.equipmentChoices))
        clazz.put("hitDie", data.hitDie)
        clazz.put("isHomebrew", data.isHomebrew)
        clazz.put("level", data.level)
        clazz.put("proficiencies", arrayConverter(data.proficiencies))
        clazz.put("proficiencyChoicesByString", data.proficiencyChoicesByString)
        if(data.spellCasting != null) clazz.put("spellCasting", JSONObject(data.spellCasting))

        if(data.subclassId != null) {
            val subclass = JSONObject()
            subclass.put("subclassId", data.subclassId)
            subclass.put("isHomebrew", data.isHomebrew)
            if(data.subclass_spell_casting != null) subclass.put("spellCasting", JSONObject(data.subclass_spell_casting))
            subclass.put("spellAreFree", data.spellAreFree)
            subclass.put("name", data.name)

            clazz.put("subclass", subclass)
        }



        classes.put(data.name, clazz)
    }

    return classes
}

private const val PATH = "character"

fun Routing.characterService(db: Database, httpClient: HttpClient) {
    post("character/postCharacter") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val character = gson.fromJson(response, Characters::class.java)
            db.characterQueries.insertOrReplace(character.copy(characterOwner = userInfo.id))
            call.respondText(character.id.toString())
        }
    }

    post("character/setHp") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterQueries.setHp(body.getLong("hp"), body.getLong("id"), userInfo.id)
            call.respond(body.getLong("id"))
        }
    }


    delete("character/deleteCharacter/{id}") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            call.parameters["id"]?.let { db.characterQueries.delete(id = it.toLong(), owner = userInfo.id) }
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("character/resetClassSpells/{characterId}/{classId}") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.characterClassSpellCrossRefQueries.deleteByCharacterId(
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Reset")
        }
    }

    delete("character/deleteClass/{characterId}/{classId}") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.characterClassCrossRefQueries.delete(
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
                owner = userInfo.id
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    //TODO this ignores owners and is only for testing remove for prod.
    get("character/getAllCharacters") {
        val list = db.characterQueries.selectAll().executeAsList()
        call.respondText(gson.toJson(list).clean())
    }

    get("character/raceChoiceData") {
        withUserInfo {
            val value = db.raceChoiceEntityQueries.select(
                characterId = call.parameters["characterId"]!!.toLong(),
                owner = it.id
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/characterClasses") {
        withUserInfo {
            val value = db.characterQueries.selectClasses(
                characterId = call.parameters["characterId"]!!.toLong(),
                owner = it.id
            )
            call.respondText(serializeCharacterClasses(value).toString())
        }
    }

    get("character/subraceChoiceData") {
        withUserInfo {
            val value = db.subraceChoiceEntityQueries.select(
                characterId = call.parameters["characterId"]!!.toLong(),
                owner = it.id
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/numberOfPreparedSpells") {
        withUserInfo {
            val value = db.characterClassSpellCrossRefQueries.getNumPreped(
                characterId = call.parameters["characterId"]!!.toLong(),
                owner = it.id,
                classId = call.parameters["classId"]!!.toLong()
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/classFeatures") {
        withUserInfo {
            val value = db.classesQueries.getFeatures(
                owner = it.id,
                classId = call.parameters["classId"]!!.toLong(),
                maxLevel = call.parameters["maxLevel"]!!.toLong()
            ).executeAsList()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/pactMagicSpells") {
        withUserInfo {
            val value = db.characterQueries.selectPactMagicSpells(
                owner = it.id,
                classId = call.parameters["classId"]!!.toLong(),
                characterId = call.parameters["characterId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/pactSlots") {
        withUserInfo {
            val value = db.characterQueries.selectPactSlots(
                owner = it.id,
                classId = call.parameters["classId"]!!.toLong(),
                characterId = call.parameters["characterId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(value.toString())
        }
    }

    get("character/FeatureChoiceChosen") {
        withUserInfo {
            val value = db.characterQueries.selectFeatureChoiceChosen(
                owner = it.id,
                choiceId = call.parameters["choiceId"]!!.toLong(),
                characterId = call.parameters["characterId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/isFeatureActive") {
        withUserInfo {
            val value = db.characterQueries.isFeatureActive(
                owner = it.id,
                featureId = call.parameters["featureId"]!!.toLong(),
                characterId = call.parameters["characterId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/backpack") {
        withUserInfo {
            val value = db.characterQueries.selectBackpack(
                owner = it.id,
                id = call.parameters["id"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/featChoiceChosen") {
        withUserInfo {
            val value = db.characterQueries.selectChosenFeats(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                choiceId = call.parameters["choiceId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/classFeats") {
        withUserInfo {
            val value = db.characterQueries.selectClassFeats(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
            ).executeAsList()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/spellCastingForClass") {
        withUserInfo {
            val value = db.characterQueries.selectSpellCastingForClass(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
            ).executeAsOneOrNull()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/spellCastingForSubclass") {
        withUserInfo {
            val value = db.characterQueries.selectSpellCastingForSubclass(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                subclassId = call.parameters["subclassId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/classChoice") {
        withUserInfo {
            val value = db.classChoiceEntityQueries.select(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }

    get("character/backgroundChoice") {
        withUserInfo {
            val value = db.classChoiceEntityQueries.select(
                owner = it.id,
                characterId = call.parameters["characterId"]!!.toLong(),
                classId = call.parameters["classId"]!!.toLong(),
            ).executeAsOne()
            call.respondText(gson.toJson(value))
        }
    }



    //Due to constraints of sqldelight this runs several queries which is quite inefficient update this should it become possible.
    get("character/allSpellsByList") {
        withUserInfo {
            val lists : List<Long> = gson.fromJson(call.parameters["classIds"], object : TypeToken<List<Long>>() {}.type)
            val response = JSONArray()
            lists.forEach { list ->
                val spells = db.characterQueries.selectAllSpellsByList(
                    owner = it.id,
                    list = list,
                    id = call.parameters["id"]!!.toLong(),
                ).executeAsList()
                response.put(JSONArray(gson.toJson(spells)))
            }

            call.respondText(response.toString())
        }
    }

    get("character/unfilledCharacter") {
        withUserInfo {
            val value = db.characterViewQueries.getById(id = call.parameters["characterId"]!!.toLong(), owner = it.id)
            call.respondText(serializeUnfilledCharacter(value.executeAsOne()).toString())
        }
    }


    webSocket("character/getLiveCharacters") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            db.characterViewQueries.selectAllFor(userInfo.id).asFlow().collect { x ->
                val characters = x.executeAsList().map {
                    serializeUnfilledCharacter(it)
                }
                send(Frame.Text(characters.toString()))
            }
        }
    }

    webSocket("character/getLiveCharacter") {
        getSession(call)?.let { session ->
            val userInfo = getUserInfo(httpClient, session, call)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                try {
                    db.characterViewQueries.getById(receivedText.toLong(), owner = userInfo.id).asFlow().collect {
                        val character = serializeUnfilledCharacter(it.executeAsOne())

                        //Send the converted json.
                        send(Frame.Text(character.toString().clean()))
                    }

                } catch (e: NumberFormatException) {
                    send(Frame.Text("Invalid Id"))
                }
            }
        }
    }

    post("character/addBackground") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterBackgroundCrossRefQueries.insert(
                characterId = body.getLong("characterId"),
                backgroundId = body.getLong("backgroundId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }
    
    post("character/insertBackgroundChoice") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.backgroundChoiceEntityQueries.insert(
                BackgroundChoiceEntity = BackgroundChoiceEntity(
                    characterId = body.getLong("characterId"),
                    backgroundId = body.getLong("backgroundId"),
                    languageChoices = Json.decodeFromString(body.getJSONObject("languageChoices").toString()),
                    owner = userInfo.id
                )
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }
    
    post("character/addClassFeat") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.classFeatCrossRefQueries.insert(
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                featId = body.getLong("featId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/addSubclassSpell") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.subclassSpellCastingSpellCrossRefQueries.insert(
                spellId = body.getLong("spellId"),
                characterId = body.getLong("characterId"),
                subclassId = body.getLong("subclassId"),
                owner = userInfo.id,
                isPrepared = try { body.getLong("isPrepared")} catch(e: Exception) { null }
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/addClassSpell") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterClassSpellCrossRefQueries.insert(
                spellId = body.getLong("spellId"),
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                owner = userInfo.id,
                isPrepared = try { body.getLong("isPrepared")} catch(e: Exception) { null }
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/insertFeatureChoice") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.featureChoiceChoiceEntityQueries.insert(
                featureId = body.getLong("featureId"),
                characterId = body.getLong("characterId"),
                choiceId = body.getLong("choiceId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }


    post("character/updateBackpack") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateBackPack(
                jsonObjectAdapter.decode(body.getString("backpack")),
                body.getLong("id"),
                userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/addSubclass") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterSubclassCrossRefQueries.insert(
                subClassId = body.getLong("subclassId"),
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/insertPactMagicState") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.pactMagicStateEntityQueries.insertOrReplace(
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                slotsCurrentAmount = body.getLong("slotsCurrentAmount"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }
    

    post("character/updateDeathSaveSuccesses") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateDeathSaveSuccesses(
                body.getLong("successes"),
                body.getLong("id"),
                userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/updateDeathSaveFailures") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateDeathSaveFailures(
                body.getLong("failures"),
                body.getLong("id"),
                userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/insertSpellSlots") {
        withUserInfo { userInfo: UserInfo ->
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateSpellSlots(
                spellSlots = jsonListAdapter.decode(body.getJSONArray("spellSlots").toString()),
                id = body.getLong("id"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("character/changeName") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateName(
                name = body.getString("name"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/changeBonds") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateBonds(
                bonds = body.getString("it"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/changePersonalityTraits") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updatePersonalityTraits(
                personalityTraits = body.getString("it"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/changeNotes") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateNotes(
                notes = body.getString("it"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/changeFlaws") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateFlaws(
                flaws = body.getString("it"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/changeIdeals") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateIdeals(
                ideals = body.getString("it"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }


    post("$PATH/heal") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.heal(
                hp = body.getLong("hp"),
                maxHp = body.getLong("maxHp"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/damage") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.damage(
                hp = body.getLong("damage"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/setTemp") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterQueries.updateTempHp(
                hp = body.getLong("temp"),
                id = body.getLong("id"),
                owner = it.id
            )
            call.respond(status = HttpStatusCode.OK, "Updated")
        }
    }

    post("$PATH/addClass") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterClassCrossRefQueries.insert(
                CharacterClassCrossRef(
                    characterId = body.getLong("characterId"),
                    classId = body.getLong("classId"),
                    owner = it.id
                )
            )
            call.respond(body.getLong("characterId"))
        }
    }

    post("$PATH/removeClass") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterClassCrossRefQueries.delete(
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                owner = it.id
            )
        }
    }

    post("$PATH/addClassChoice") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.classChoiceEntityQueries.insertClassChoiceEntity(
                characterId = body.getLong("characterId"),
                classId = body.getLong("classId"),
                owner = it.id,
                level = body.getLong("level"),
                isBaseClass = body.getBoolean("isBaseClass"),
                totalNumOnGoldDie = body.getLong("totalNumOnGoldDie"),
                abilityImprovementsGranted = jsonListAdapter.decode((body.getString("abilityImprovementsGranted"))),
                tookGold = body.getBoolean("tookGold"),
                proficiencyChoicesByString = jsonListAdapter.decode(body.getString("proficiencyChoicesByString"))
            )
        }
    }

    post("$PATH/addRace") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterRaceCrossRefQueries.insert(
                id = body.getLong("characterId"),
                raceId = body.getLong("raceId"),
                owner = it.id
            )
        }
    }

    post("$PATH/addSubrace") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.characterSubraceCrossRefQueries.insert(
                characterId = body.getLong("characterId"),
                subraceId = body.getLong("subraceId"),
                owner = it.id
            )
        }
    }

    post("$PATH/addSubraceChoice") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.subraceChoiceEntityQueries.insert(
                characterId = body.getLong("characterId"),
                subraceId = body.getLong("subraceId"),
                owner = it.id,
                abcchosenByString = body.getString("abilityBonusChoice"),
                languageChoice = body.getString("languageChoice"),
                abilityBonusOverrides = body.getString("abilityBonusOverrides")
            )
        }
    }

    post("$PATH/addRaceChoice") {
        withUserInfo {
            val body = JSONObject(call.receiveText())
            db.raceChoiceEntityQueries.insert(
                characterId = body.getLong("id"),
                raceId = body.getLong("raceId"),
                owner = it.id,
                abcchosenByString = body.getString("abilityBonusChoice"),
                languageChoice = body.getString("languageChoice"),
                abilityBonusOverrides = body.getString("abilityBonusOverrides"),
                proficiencyChoice = body.getString("proficiencyChoice")
            )
        }
    }
}
