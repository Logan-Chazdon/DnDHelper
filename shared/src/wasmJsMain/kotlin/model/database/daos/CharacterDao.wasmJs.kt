package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity
import services.CharacterService


actual abstract class CharacterDao {
    protected val characterService: CharacterService
    constructor(characterService: CharacterService) {
        this.characterService = characterService
    }

    actual suspend fun insertCharacter(character: CharacterEntity): Long {
        return characterService.postCharacter(character)
    }

    actual suspend fun updateCharacter(character: CharacterEntity) {
    }

    actual abstract suspend fun getSpellCastingSpellsForSubclass(
        characterId: Int,
        subclassId: Int
    ): Map<Spell, Boolean?>

    actual abstract suspend fun getSpellCastingSpellsForClass(
        characterId: Int,
        classId: Int
    ): Map<Spell, Boolean?>

    actual abstract suspend fun getClassFeats(
        classId: Int,
        characterId: Int
    ): MutableList<Feat>

    actual abstract suspend fun getFeatChoiceChosen(
        characterId: Int,
        choiceId: Int
    ): List<Feat>

    actual suspend fun getClassChoiceData(
        characterId: Int,
        classId: Int
    ): ClassChoiceEntity {
        return characterService.getClassChoiceData(characterId, classId)
    }

    actual suspend fun insertPactMagicStateEntity(
        characterId: Int,
        classId: Int,
        slotsCurrentAmount: Int
    ) {
        characterService.insertPactMagicStateEntity(characterId, classId, slotsCurrentAmount)
    }

    actual fun getAllCharacters(): Flow<List<Character>> {
        return characterService.getAllCharacters()
    }

    actual abstract suspend fun deleteCharacter(id: Int)
    actual suspend fun insertCharacterSubRaceCrossRef(characterId: Int, subraceId: Int) {
        characterService.insertCharacterSubraceCrossRef(characterId, subraceId)
    }

    actual suspend fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        characterService.insertSubraceChoiceEntity(subraceChoiceEntity)
    }

    actual suspend fun insertCharacterSubclassCrossRef(subClassId: Int, characterId: Int, classId: Int) {
        characterService.insertCharacterSubclassCrossRef(subClassId, characterId, classId)
    }

    actual suspend fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
        characterService.insertFeatureChoiceEntity(featureId, characterId, choiceId)
    }

    actual suspend fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        characterService.insertCharacterClassSpellCrossRef(classId, spellId, characterId, isPrepared)
    }

    actual suspend fun insertSubClassSpellCastingCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        characterService.insertSubClassSpellCastingCrossRef(subclassId, spellId, characterId, isPrepared)
    }

    actual abstract suspend fun getCharacterBackPack(id: Int): Backpack
    actual abstract suspend fun insertCharacterBackPack(backpack: Backpack, id: Int)
    actual suspend fun removeCharacterClassCrossRef(characterId: Int, classId: Int) {
        characterService.removeCharacterClassCrossRef(characterId, classId)
    }

    actual suspend fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        characterService.insertCharacterClassCrossRef(characterId, classId)
    }

    actual suspend fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        characterService.insertClassChoiceEntity(classChoiceEntity)
    }

    actual suspend fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
        characterService.insertCharacterClassFeatCrossRef(characterId, featId, classId)
    }

    actual suspend fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        characterService.insertBackgroundChoiceEntity(backgroundChoiceEntity)
    }

    actual suspend fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
        characterService.insertRaceChoice(raceChoiceEntity)
    }

    actual abstract suspend fun insertCharacterRaceCrossRef(id: Int, raceId: Int)
    actual suspend fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        characterService.insertCharacterBackgroundCrossRef(backgroundId, characterId)
    }

    actual abstract suspend fun getAllSpellsByList(
        id: Int,
        classIdsByName: List<Int>
    ): Map<Spell, Boolean?>

    actual abstract suspend fun isFeatureActive(featureId: Int, characterId: Int): Boolean
    actual abstract suspend fun getFeatureChoiceChosen(
        choiceId: Int,
        characterId: Int
    ): List<Feature>

    actual abstract suspend fun findCharacterWithoutListChoices(id: Int): Character
    actual abstract fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character>
    actual suspend fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity {
        return characterService.getRaceChoiceData(raceId, charId)
    }

    actual suspend fun getSubraceChoiceData(
        subraceId: Int,
        charId: Int
    ): SubraceChoiceEntity {
        return characterService.getSubraceChoiceData(subraceId, charId)
    }

    actual suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return characterService.getBackgroundChoiceData(charId)
    }

    actual abstract suspend fun getCharactersClasses(characterId: Int): MutableMap<String, Class>
    actual abstract suspend fun getCharacterPactSlots(classId: Int, characterId: Int): Int
    actual abstract suspend fun getPactMagicSpells(
        characterId: Int,
        classId: Int
    ): MutableList<Spell>

    actual abstract suspend fun getClassFeatures(
        classId: Int,
        maxLevel: Int
    ): MutableList<Feature>

    actual abstract suspend fun setTemp(id: Int, temp: Int)
    actual abstract suspend fun heal(id: Int, hp: Int, maxHp: Int)
    actual abstract suspend fun damage(id: Int, damage: Int)
    actual abstract suspend fun setHp(id: Int, hp: Int)
    actual abstract suspend fun updateDeathSaveSuccesses(id: Int, it: Int)
    actual abstract suspend fun updateDeathSaveFailures(id: Int, it: Int)
    actual abstract suspend fun insertSpellSlots(spellSlots: List<Resource>, id: Int)
    actual abstract suspend fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)
    actual abstract suspend fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int
    actual abstract suspend fun changeName(it: String, id: Int)
    actual abstract suspend fun setPersonalityTraits(it: String, id: Int)
    actual abstract suspend fun setIdeals(it: String, id: Int)
    actual abstract suspend fun setNotes(it: String, id: Int)
    actual abstract suspend fun setFlaws(it: String, id: Int)
    actual abstract suspend fun setBonds(it: String, id: Int)
    actual suspend fun insertCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean) {
    }
}