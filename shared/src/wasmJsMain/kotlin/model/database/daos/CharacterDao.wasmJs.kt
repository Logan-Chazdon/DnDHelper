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

    actual fun updateCharacter(character: CharacterEntity) {
    }

    actual abstract fun getSpellCastingSpellsForSubclass(
        characterId: Int,
        subclassId: Int
    ): Map<Spell, Boolean?>

    actual abstract fun getSpellCastingSpellsForClass(
        characterId: Int,
        classId: Int
    ): Map<Spell, Boolean?>

    actual abstract fun getClassFeats(
        classId: Int,
        characterId: Int
    ): MutableList<Feat>

    actual abstract fun getFeatChoiceChosen(
        characterId: Int,
        choiceId: Int
    ): List<Feat>

    actual fun getClassChoiceData(
        characterId: Int,
        classId: Int
    ): ClassChoiceEntity {
        TODO("Not yet implemented")
    }

    actual fun insertPactMagicStateEntity(
        characterId: Int,
        classId: Int,
        slotsCurrentAmount: Int
    ) {
    }

    actual fun getAllCharacters(): Flow<List<Character>> {
        return characterService.getAllCharacters()
    }

    actual abstract suspend fun deleteCharacter(id: Int)
    actual fun insertCharacterSubRaceCrossRef(characterId: Int, subraceId: Int) {
    }

    actual fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
    }

    actual fun insertCharacterSubclassCrossRef(subClassId: Int, characterId: Int, classId: Int) {
    }

    actual fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
    }

    actual fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
    }

    actual fun insertSubClassSpellCastingCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
    }

    actual abstract fun getCharacterBackPack(id: Int): Backpack
    actual abstract fun insertCharacterBackPack(backpack: Backpack, id: Int)
    actual fun removeCharacterClassCrossRef(characterId: Int, classId: Int) {
    }

    actual fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
    }

    actual fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
    }

    actual fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
    }

    actual fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
    }

    actual fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
    }

    actual abstract fun insertCharacterRaceCrossRef(id: Int, raceId: Int)
    actual fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
    }

    actual abstract fun getAllSpellsByList(
        id: Int,
        classIdsByName: List<Int>
    ): Map<Spell, Boolean?>

    actual abstract fun isFeatureActive(featureId: Int, characterId: Int): Boolean
    actual abstract fun getFeatureChoiceChosen(
        choiceId: Int,
        characterId: Int
    ): List<Feature>

    actual abstract fun findCharacterWithoutListChoices(id: Int): Character
    actual abstract fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character>
    actual fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity {
        TODO("Not yet implemented")
    }

    actual fun getSubraceChoiceData(
        subraceId: Int,
        charId: Int
    ): SubraceChoiceEntity {
        TODO("Not yet implemented")
    }

    actual fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        TODO("Not yet implemented")
    }

    actual abstract fun getCharactersClasses(characterId: Int): MutableMap<String, Class>
    actual abstract fun getCharacterPactSlots(classId: Int, characterId: Int): Int
    actual abstract fun getPactMagicSpells(
        characterId: Int,
        classId: Int
    ): MutableList<Spell>

    actual abstract fun getClassFeatures(
        classId: Int,
        maxLevel: Int
    ): MutableList<Feature>

    actual abstract fun setTemp(id: Int, temp: Int)
    actual abstract fun heal(id: Int, hp: Int, maxHp: Int)
    actual abstract fun damage(id: Int, damage: Int)
    actual abstract fun setHp(id: Int, hp: Int)
    actual abstract fun updateDeathSaveSuccesses(id: Int, it: Int)
    actual abstract fun updateDeathSaveFailures(id: Int, it: Int)
    actual abstract fun insertSpellSlots(spellSlots: List<Resource>, id: Int)
    actual abstract fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)
    actual abstract fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int
    actual abstract fun changeName(it: String, id: Int)
    actual abstract fun setPersonalityTraits(it: String, id: Int)
    actual abstract fun setIdeals(it: String, id: Int)
    actual abstract fun setNotes(it: String, id: Int)
    actual abstract fun setFlaws(it: String, id: Int)
    actual abstract fun setBonds(it: String, id: Int)
    actual fun insertCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean) {
    }
}