package model.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import model.Character
import model.choiceEntities.*
import model.junctionEntities.CharacterRaceCrossRef
import model.junctionEntities.CharacterSubclassCrossRef
import model.junctionEntities.CharacterSubraceCrossRef




@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class CharacterRepository {
    fun insertPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int)
    fun getAllCharacters(): Flow<List<model.Character>>
    fun removeFeatureChoiceChoiceEntity(choiceId: Int, characterId: Int)
    fun insertCharacter(character: model.CharacterEntity)
    fun deleteCharacterById(id: Int)
    fun createDefaultCharacter(): Int
    fun insertCharacterSubraceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef)
    fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)
    fun insertCharacterSubclassCrossRef(characterSubclassCrossRef: CharacterSubclassCrossRef)
    fun insertFeatureChoiceChoiceEntity(featureChoiceChoiceEntity: FeatureChoiceChoiceEntity)
    fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        prepared: Boolean?
    )

    fun insertSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    )

    fun insertCharacterClassEquipment(
        equipmentChoices: List<model.ItemChoice>,
        equipment: List<model.ItemInterface>,
        characterId: Int
    )

    fun setClassGold(gold: Int, characterId: Int)
    fun setBackgroundCurrency(backgroundCurrencyMap: Map<String, model.Currency>, characterId: Int)
    fun removeClassFromCharacter(classId: Int, characterId: Int)
    fun insertCharacterClassCrossRef(characterId: Int, classId: Int)
    fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity)
    fun addFeatsToCharacterClass(characterId: Int, classId: Int, feats: List<model.Feat>)
    fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)
    fun insertRaceChoiceEntity(raceChoiceEntity: RaceChoiceEntity)
    fun insertCharacterRaceCrossRef(characterRaceCrossRef: CharacterRaceCrossRef)
    fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int)
    fun getSpellsForCharacter(character: model.Character): MutableMap<Int, MutableList<Pair<Boolean?, model.Spell>>>
    fun getCharacterById(id: Int): model.Character
    fun getLiveCharacterById(
        id: Int,
        character: MutableStateFlow<Character>,
        characterKey: Flow<Int>? = null
    )

    fun setTemp(id: Int?, temp: String)
    fun heal(id: Int?, hp: String, maxHp: Int)
    fun setHp(id: Int?, hp: String)
    fun damage(id: Int?, damage: String)
    fun updateDeathSaveSuccesses(id: Int?, it: Boolean)
    fun updateDeathSaveFailures(id: Int?, it: Boolean)
    fun insertSpellSlots(spellSlots: List<model.Resource>, id: Int)
    fun removeClassSpellCrossRefs(classId: Int, characterId: Int)
    fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int
    fun changeName(it: String, id: Int)
    fun setPersonalityTraits(it: String, id: Int)
    fun setIdeals(it: String, id: Int)
    fun setBonds(it: String, id: Int)
    fun setFlaws(it: String, id: Int)
    fun setNotes(it: String, id: Int)
    fun activateInfusion(infusionId: Int, characterId: Int)
    fun deactivateInfusion(infusionId: Int, characterId: Int)
    fun removeFeatureChoiceCrossRefs(clazz: model.Class, characterId: Int)

    companion object {
        val statNames: List<String>
        val shortStatNames: List<String>
    }
}


