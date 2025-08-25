package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class CharacterDao {
    suspend fun insertCharacter(character: CharacterEntity): Long
    suspend fun updateCharacter(character: CharacterEntity)
    abstract suspend fun getSpellCastingSpellsForSubclass(
        characterId: Int,
        subclassId: Int
    ): Map<Spell, Boolean?>

    abstract suspend fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?>
    abstract suspend fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat>
    abstract suspend fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>
    suspend fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity
    suspend fun insertPactMagicStateEntity(
        characterId: Int,
        classId: Int,
        slotsCurrentAmount: Int
    )

    fun getAllCharacters(): Flow<List<Character>>
    abstract suspend fun deleteCharacter(id: Int)
    suspend fun insertCharacterSubRaceCrossRef(characterId: Int, subraceId: Int)
    suspend fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)
    suspend fun insertCharacterSubclassCrossRef(
        subClassId: Int,
        characterId: Int,
        classId: Int,
    )

    suspend fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int)
    suspend fun insertCharacterClassSpellCrossRef(classId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    suspend fun insertSubClassSpellCastingCrossRef(subclassId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    abstract suspend fun getCharacterBackPack(id: Int): Backpack
    abstract suspend fun insertCharacterBackPack(backpack: Backpack, id: Int)
    suspend fun removeCharacterClassCrossRef(characterId: Int, classId: Int)
    suspend fun insertCharacterClassCrossRef(characterId: Int, classId: Int)
    suspend fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity)
    suspend fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int)
    suspend fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)
    suspend fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity)
    abstract suspend fun insertCharacterRaceCrossRef(id: Int, raceId: Int)
    suspend fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int)
    abstract suspend fun getAllSpellsByList(id: Int, classIdsByName: List<Int>):  Map<Spell, Boolean?>
    abstract suspend fun isFeatureActive(featureId: Int, characterId: Int): Boolean
    abstract suspend fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature>
    abstract suspend fun findCharacterWithoutListChoices(id: Int): Character
    abstract fun findLiveCharacterWithoutListChoices(id: Int) : Flow<Character>
    suspend fun getRaceChoiceData(raceId: Int, charId: Int) : RaceChoiceEntity
    suspend fun getSubraceChoiceData(subraceId: Int, charId: Int) : SubraceChoiceEntity
    suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    suspend fun getCharactersClasses(characterId: Int): MutableMap<String, Class>
    abstract suspend fun getCharacterPactSlots(classId: Int, characterId: Int): Int
    abstract suspend fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell>
    abstract suspend fun getClassFeatures(classId: Int, maxLevel: Int = 20): MutableList<Feature>
    abstract suspend fun setTemp(id: Int, temp: Int)
    abstract suspend fun heal(id: Int, hp: Int, maxHp: Int)
    abstract suspend fun damage(id: Int, damage: Int)
    abstract suspend fun setHp(id: Int, hp: Int)
    abstract suspend fun updateDeathSaveSuccesses(id: Int, it: Int)
    abstract suspend fun updateDeathSaveFailures(id: Int, it: Int)
    abstract suspend fun insertSpellSlots(spellSlots: List<Resource>, id: Int)
    abstract suspend fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)
    abstract suspend fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int
    abstract suspend fun changeName(it: String, id: Int)
    abstract suspend fun setPersonalityTraits(it: String, id: Int)
    abstract suspend fun setIdeals(it: String, id: Int)
    abstract suspend fun setNotes(it: String, id: Int)
    abstract suspend fun setFlaws(it: String, id: Int)
    abstract suspend fun setBonds(it: String, id: Int)
    suspend fun insertCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean)
}