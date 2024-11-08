package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity
import model.converters.UnfilledCharacterSerializer

class CharacterService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        AllCharacters("$PATH/getLiveCharacters"),
        LiveCharacter("$PATH/getLiveCharacter"),
        PostCharacter("$PATH/postCharacter"),
        DeleteCharacter("$PATH/deleteCharacter"),
        SetHp("$PATH/setHp"),
        UpdateDeathSaveSuccesses("$PATH/updateDeathSaveSuccesses"),
        UpdateDeathSaveFailures("$PATH/updateDeathSaveFailures"),
        InsertSpellSlots("$PATH/insertSpellSlots"),
        ChangeName("$PATH/changeName"),
        ChangePersonalityTraits("$PATH/changePersonalityTraits"),
        ChangeIdeals("$PATH/changeIdeals"),
        ChangeBonds("$PATH/changeBonds"),
        ChangeNotes("$PATH/changeNotes"),
        ChangeFlaws("$PATH/changeFlaws"),
        Heal("$PATH/heal"),
        Damage("$PATH/damage"),
        SetTemp("$PATH/setTemp"),
        AddClass("$PATH/addClass"),
        RemoveClass("$PATH/removeClass"),
        AddClassChoice("$PATH/addClassChoice"),
        AddRace("$PATH/addRace"),
        AddSubrace("$PATH/addSubrace"),
        AddSubraceChoice("$PATH/addSubraceChoice"),
        AddRaceChoice("$PATH/addRaceChoice"),
        RaceChoiceData("$PATH/raceChoiceData"),
        SubraceChoiceData("$PATH/subraceChoiceData"),
        GetNumPreped("$PATH/numberOfPreparedSpells"),
        ClassFeatures("$PATH/classFeatures"),
        PactMagicSpells("$PATH/pactMagicSpells"),
        PactSlots("$PATH/pactSlots"),
        FeatureChoiceChosen("$PATH/featureChoiceChosen"),
        IsFeatureActive("$PATH/isFeatureActive"),
        AllSpellsByList("$PATH/allSpellsByList"),
        UpdateBackpack("$PATH/updateBackpack"),
        Backpack("$PATH/backpack"),
        FeatChoiceChosen("$PATH/featChoiceChosen"),
        ClassFeats("$PATH/classFeats"),
        SpellCastingForClass("$PATH/spellCastingForClass"),
        SpellCastingForSubclass("$PATH/spellCastingForSubclass"),
        ClassChoice("$PATH/classChoice"),
        InsertPactMagicState("$PATH/insertPactMagicState"),
        AddSubclass("$PATH/addSubclass"),
        InsertFeatureChoice("$PATH/insertFeatureChoice"),
        AddClassSpell("$PATH/addClassSpell"),
        AddSubclassSpell("$PATH/addSubclassSpell"),
        AddClassFeat("$PATH/addClassFeat"),
        InsertBackgroundChoice("$PATH/insertBackgroundChoice"),
        AddBackground("$PATH/addBackground"),
        BackgroundChoice("$PATH/backgroundChoice"),
        DeleteClass("$PATH/deleteClass"),
        CharacterClasses("$PATH/characterClasses"),
        UnfilledCharacter("$PATH/unfilledCharacter"),
        ResetClassSpells("$PATH/resetClassSpells"),
    }

    companion object {
        const val PATH = "character"
    }

    /**This method emits mostly empty character objects for the main character list display do not count on the rest of the data.
     */
    fun getAllCharacters(): Flow<List<Character>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.AllCharacters.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    val myMessage = "test"
                    if (myMessage != null) {
                        send(myMessage)
                    }
                    if (othersMessage?.readText() != "received") {
                        val listToEmit = Json.decodeFromString<List<CharacterEntity>>(othersMessage!!.readText()).map {
                            Character(it.name, id = it.id)
                        }
                        emit(listToEmit)
                    }
                }
            }
        }
    }

    suspend fun postCharacter(character: CharacterEntity): Long {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.PostCharacter.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(character))
        }.bodyAsText()
        return id.toLong()
    }

    suspend fun deleteCharacter(id: Int) {
        client.delete {
            url {
                host = apiUrl
                port = targetPort
                path("${Paths.DeleteCharacter.path}/$id")
            }
        }
    }

    fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveCharacter.path) {
                while (true) {
                    send(Frame.Text(id.toString()))
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "received") {
                        val character = UnfilledCharacterSerializer.deserialize(
                            othersMessage!!.readText())
                        emit(character)
                    }
                }
            }
        }
    }


    suspend fun setHp(id: Int, hp: Int) {
        postTo(Paths.SetHp.path) {
            put("id", id)
            put("hp", hp)
        }
    }

    suspend fun updateDeathSaveSuccesses(id: Int, it: Int) =
        postTo(Paths.UpdateDeathSaveSuccesses.path) {
            put("id", id)
            put("successes", it)
        }


    suspend fun updateDeathSaveFailures(id: Int, it: Int) =
        postTo(Paths.UpdateDeathSaveFailures.path) {
            put("id", id)
            put("failures", it)
        }


    suspend fun insertSpellSlots(spellSlots: List<Resource>, id: Int) {
        postTo(Paths.InsertSpellSlots.path) {
            putJsonArray("spellSlots") {
                spellSlots.forEach { add(Json.encodeToString(it)) }
            }
            put("id", id)
        }
    }

    suspend fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int) {
        deleteFrom(Paths.ResetClassSpells.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }
    }

    suspend fun getNumOfPreparedSpells(classId: Int, characterId: Int) : Int{
        return getFrom(Paths.GetNumPreped.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText().toIntOrNull() ?: 0
    }

    suspend fun changeName(name: String, id: Int) {
        postTo(Paths.ChangeName.path) {
            put("name", name)
            put("id", id)
        }
    }

    suspend fun setPersonalityTraits(it: String, id: Int) {
        postTo(Paths.ChangePersonalityTraits.path) {
            put("it", it)
            put("id", id)
        }
    }

    suspend fun setIdeals(it: String, id: Int) {
        postTo(Paths.ChangeIdeals.path) {
            put("it", it)
            put("id", id)
        }
    }

    suspend fun setNotes(it: String, id: Int) {
        postTo(Paths.ChangeNotes.path) {
            put("it", it)
            put("id", id)
        }
    }

    suspend fun setFlaws(it: String, id: Int) {
        postTo(Paths.ChangeFlaws.path) {
            put("it", it)
            put("id", id)
        }
    }

    suspend fun setBonds(it: String, id: Int) {
        postTo(Paths.ChangeBonds.path) {
            put("it", it)
            put("id", id)
        }
    }

    suspend fun damage(id: Int, damage: Int) {
        postTo(Paths.Damage.path) {
            put("damage", damage)
            put("id", id)
        }
    }

    suspend fun heal(id: Int, hp: Int, maxHp: Int) {
        postTo(Paths.Heal.path) {
            put("hp", hp)
            put("maxHp", maxHp)
            put("id", id)
        }
    }

    suspend fun setTemp(id: Int, temp: Int) {
        postTo(Paths.SetTemp.path) {
            put("temp", temp)
            put("id", id)
        }
    }

    suspend fun getClassFeatures(classId: Int, maxLevel: Int): MutableList<Feature> {
        return Json.decodeFromString(getFrom(Paths.ClassFeatures.path) {
            append("classId", classId.toString())
            append("maxLevel", maxLevel.toString())
        }.bodyAsText())
    }

    suspend fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell> {
        return Json.decodeFromString(getFrom(Paths.PactMagicSpells.path) {
            append("characterId", characterId.toString())
            append("classId", classId.toString())
        }.bodyAsText())
    }

    suspend fun getCharacterPactSlots(classId: Int, characterId: Int): Int {
        return getFrom(Paths.PactSlots.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText().toIntOrNull() ?: 0
    }

    suspend fun getCharactersClasses(characterId: Int): MutableMap<String, Class> {
        return Json.decodeFromString(getFrom(Paths.CharacterClasses.path) {
            append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun findCharacterWithoutListChoices(id: Int): Character {
        return UnfilledCharacterSerializer.deserialize(getFrom(Paths.UnfilledCharacter.path) {
            append("characterId", id.toString())
        }.bodyAsText())
    }

    suspend fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature> {
        return Json.decodeFromString(getFrom(Paths.FeatureChoiceChosen.path) {
               append("choiceId", choiceId.toString())
               append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun isFeatureActive(featureId: Int, characterId: Int): Boolean {
        return getFrom(Paths.IsFeatureActive.path) {
            append("featureId", featureId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText().toIntOrNull() == 1
    }

    suspend fun getAllSpellsByList(id: Int, classIdsByName: List<Int>): Map<Spell, Boolean?> {
        return Json.decodeFromString(getFrom(Paths.AllSpellsByList.path) {
            append("id", id.toString())
            append("classIds", Json.encodeToString(classIdsByName))
        }.bodyAsText())
    }

    suspend fun insertCharacterRaceCrossRef(id: Int, raceId: Int) {
        postTo(Paths.AddRace.path) {
            put("characterId", id)
            put("raceId", raceId)
        }
    }

    suspend fun insertCharacterBackPack(backpack: Backpack, id: Int) {
        postTo(Paths.UpdateBackpack.path) {
            put("backpack", Json.encodeToString(backpack))
            put("id", id.toString())
        }
    }

    suspend fun getCharacterBackPack(id: Int): Backpack {
        return Json.decodeFromString(getFrom(Paths.Backpack.path) {
            append("id", id.toString())
        }.bodyAsText())
    }

    suspend fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat> {
        return Json.decodeFromString(getFrom(Paths.FeatChoiceChosen.path) {
            append("characterId", characterId.toString())
            append("choiceId", choiceId.toString())
        }.bodyAsText())
    }

    suspend fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat> {
        return Json.decodeFromString(getFrom(Paths.ClassFeats.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?> {
        return Json.decodeFromString(getFrom(Paths.SpellCastingForClass.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun getSpellCastingSpellsForSubclass(characterId: Int, subclassId: Int): Map<Spell, Boolean?> {
        return Json.decodeFromString(getFrom(Paths.SpellCastingForSubclass.path) {
            append("subclassId", subclassId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity {
        return Json.decodeFromString(getFrom(Paths.ClassChoice.path) {
            append("classId", classId.toString())
            append("characterId", characterId.toString())
        }.bodyAsText())
    }

    suspend fun insertPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int) {
        postTo(Paths.InsertPactMagicState.path) {
            put("characterId", characterId)
            put("classId", classId)
            put("slotsCurrentAmount", slotsCurrentAmount)
        }
    }

    suspend fun insertCharacterSubraceCrossRef(characterId: Int, subraceId: Int) {
        postTo(Paths.AddSubrace.path) {
            put("characterId", characterId)
            put("subrace", subraceId)
        }
    }

    suspend fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        postTo(Paths.AddSubraceChoice.path) {
            put("id", subraceChoiceEntity.characterId)
            put("subraceId", subraceChoiceEntity.subraceId)
            put("languageChoice", Json.encodeToString(subraceChoiceEntity.languageChoice))
            put("abilityBonusChoice", Json.encodeToString(subraceChoiceEntity.abilityBonusChoice))
            put("abilityBonusOverrides", Json.encodeToString(subraceChoiceEntity.abilityBonusOverrides))
        }
    }

    suspend fun insertCharacterSubclassCrossRef(subClassId: Int, characterId: Int, classId: Int) {
        postTo(Paths.AddSubclass.path) {
            put("subclassId", subClassId)
            put("characterId", characterId)
            put("classId", classId)
        }
    }

    suspend fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
        postTo(Paths.InsertFeatureChoice.path) {
            put("featureId", featureId)
            put("characterId", characterId)
            put("choiceId", choiceId)
        }
    }

    suspend fun insertCharacterClassSpellCrossRef(classId: Int, spellId: Int, characterId: Int, prepared: Boolean?) {
        postTo(Paths.AddClassSpell.path) {
            put("classId", classId)
            put("spellId", spellId)
            put("characterId", characterId)
            put("prepared", prepared)
        }
    }

    suspend fun insertSubClassSpellCastingCrossRef(subclassId: Int, spellId: Int, characterId: Int, prepared: Boolean?) {
        postTo(Paths.AddSubclassSpell.path) {
            put("subclassId", subclassId)
            put("spellId", spellId)
            put("characterId", characterId)
            put("prepared", prepared)
        }
    }

    suspend fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        postTo(Paths.AddClass.path) {
            put("characterId", characterId)
            put("classId", classId)
        }
    }

    suspend fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        postTo(Paths.AddClassChoice.path) {
            put("classId", classChoiceEntity.classId)
            put("characterId", classChoiceEntity.characterId)
            put("isBaseClass", classChoiceEntity.isBaseClass)
            put("level", classChoiceEntity.level)
            put("tookGold", classChoiceEntity.tookGold)
            put("totalNumOnGoldDie", classChoiceEntity.totalNumOnGoldDie)
            put("abilityImprovementsGranted", Json.encodeToString(classChoiceEntity.abilityImprovementsGranted))
            put("proficiencyChoicesByString", Json.encodeToString(classChoiceEntity.proficiencyChoicesByString))
        }
    }

    suspend fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
        postTo(Paths.AddClassFeat.path) {
            put("characterId", characterId)
            put("featId", featId)
            put("classId", classId)
        }
    }

    suspend fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        postTo(Paths.InsertBackgroundChoice.path) {
            put("characterId", backgroundChoiceEntity.characterId)
            put("backgroundId", backgroundChoiceEntity.backgroundId)
            put("languageChoices", Json.encodeToString(backgroundChoiceEntity.languageChoices))
        }
    }

    suspend fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
        postTo(Paths.AddRaceChoice.path) {
            put("id", raceChoiceEntity.characterId)
            put("raceId", raceChoiceEntity.raceId)
            put("languageChoice", Json.encodeToString(raceChoiceEntity.languageChoice))
            put("proficiencyChoice", Json.encodeToString(raceChoiceEntity.proficiencyChoice))
            put("abilityBonusChoice", Json.encodeToString(raceChoiceEntity.abilityBonusChoice))
            put("abilityBonusOverrides", Json.encodeToString(raceChoiceEntity.abilityBonusOverrides))
        }
    }

    suspend fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        postTo(Paths.AddBackground.path) {
            put("backgroundId", backgroundId)
            put("characterId", characterId)
        }
    }

    suspend fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity {
        return Json.decodeFromString(getFrom(Paths.RaceChoiceData.path) {
            append("raceId", raceId.toString())
            append("characterId", charId.toString())
        }.bodyAsText())
    }

    suspend fun getSubraceChoiceData(subraceId: Int, charId: Int): SubraceChoiceEntity {
        return Json.decodeFromString(getFrom(Paths.SubraceChoiceData.path) {
            append("subraceId", subraceId.toString())
            append("characterId", charId.toString())
        }.bodyAsText())
    }

    suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return Json.decodeFromString(getFrom(Paths.BackgroundChoice.path) {
            append("characterId", charId.toString())
        }.bodyAsText())
    }

    suspend fun removeCharacterClassCrossRef(characterId: Int, classId: Int) {
        deleteFrom(Paths.DeleteClass.path) {
            append("characterId", characterId.toString())
            append("classId", classId.toString())
        }
    }
}