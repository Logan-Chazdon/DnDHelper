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
    fun updateCharacter(character: CharacterEntity)
    abstract fun getSpellCastingSpellsForSubclass(
        characterId: Int,
        subclassId: Int
    ): Map<Spell, Boolean?>

    abstract fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?>
    abstract fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat>
    abstract fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>
    fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity
    fun insertPactMagicStateEntity(
        characterId: Int,
        classId: Int,
        slotsCurrentAmount: Int
    )

    fun getAllCharacters(): Flow<List<Character>>
    abstract suspend fun deleteCharacter(id: Int)
    fun insertCharacterSubRaceCrossRef(characterId: Int, subraceId: Int)
    fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)
    fun insertCharacterSubclassCrossRef(
        subClassId: Int,
        characterId: Int,
        classId: Int,
    )

    fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int)
    fun insertCharacterClassSpellCrossRef(classId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    fun insertSubClassSpellCastingCrossRef(subclassId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    abstract fun getCharacterBackPack(id: Int): Backpack
    abstract fun insertCharacterBackPack(backpack: Backpack, id: Int)
    fun removeCharacterClassCrossRef(characterId: Int, classId: Int)
    fun insertCharacterClassCrossRef(characterId: Int, classId: Int)
    fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity)
    fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int)
    fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)
    fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity)
    abstract fun insertCharacterRaceCrossRef(id: Int, raceId: Int)
    fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int)
    abstract fun getAllSpellsByList(id: Int, classIdsByName: List<Int>):  Map<Spell, Boolean?>
    abstract fun isFeatureActive(featureId: Int, characterId: Int): Boolean
    abstract fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature>
    abstract fun findCharacterWithoutListChoices(id: Int): Character
    abstract fun findLiveCharacterWithoutListChoices(id: Int) : Flow<Character>
    fun getRaceChoiceData(raceId: Int, charId: Int) : RaceChoiceEntity
    fun getSubraceChoiceData(subraceId: Int, charId: Int) : SubraceChoiceEntity
    fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    abstract fun getCharactersClasses(characterId: Int): MutableMap<String, Class>
    abstract fun getCharacterPactSlots(classId: Int, characterId: Int): Int
    abstract fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell>
    abstract fun getClassFeatures(classId: Int, maxLevel: Int = 20): MutableList<Feature>
    abstract fun setTemp(id: Int, temp: Int)
    abstract fun heal(id: Int, hp: Int, maxHp: Int)
    abstract fun damage(id: Int, damage: Int)
    abstract fun setHp(id: Int, hp: Int)
    abstract fun updateDeathSaveSuccesses(id: Int, it: Int)
    abstract fun updateDeathSaveFailures(id: Int, it: Int)
    abstract fun insertSpellSlots(spellSlots: List<Resource>, id: Int)
    abstract fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)
    abstract fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int
    abstract fun changeName(it: String, id: Int)
    abstract fun setPersonalityTraits(it: String, id: Int)
    abstract fun setIdeals(it: String, id: Int)
    abstract fun setNotes(it: String, id: Int)
    abstract fun setFlaws(it: String, id: Int)
    abstract fun setBonds(it: String, id: Int)
    fun insertCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean)
}