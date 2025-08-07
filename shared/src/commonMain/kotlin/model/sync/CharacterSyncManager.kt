package model.sync

import model.Backpack
import model.CharacterEntity
import model.Resource
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class CharacterSyncManager {
    fun postCharacter(character: CharacterEntity)
    fun deleteCharacter(id: Int)
    fun updateCharacterName(id: Int, name: String)
    fun updateCharacterTraits(id: Int, name: String)
    fun updateCharacterIdeals(id: Int, name: String)
    fun updateCharacterBonds(id: Int, name: String)
    fun updateCharacterFlaws(id: Int, name: String)
    fun updateCharacterNotes(id: Int, name: String)
    fun postPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int)
    fun postCharacterSubraceCrossRef(characterId: Int, subraceId: Int)
    fun postSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)
    fun postCharacterSubclassCrossRef(subclassId: Int, characterId: Int, classId: Int)
    fun postFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int)
    fun postCharacterClassSpellCrossRef(classId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    fun postSubclassSpellCastingSpellCrossRef(subclassId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?)
    fun postCharacterBackPack(backpack: Backpack, characterId: Int)
    fun deleteClassCharacterCrossRef(characterId: Int, classId: Int)
    fun postCharacterClassCrossRef(characterId: Int, classId: Int)
    fun postClassChoiceEntity(classChoiceEntity: ClassChoiceEntity)
    fun postCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int)
    fun postBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)
    fun postRaceChoice(raceChoiceEntity: RaceChoiceEntity)
    fun postCharacterRaceCrossRef(id: Int, raceId: Int)
    fun postCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int)
    fun postTemp(id: Int?, temp: String)
    fun postHeal(id: Int?, hp: String, maxHp: Int)
    fun postHp(id: Int, toInt: Int)
    fun postDamage(id: Int, toInt: Int)
    fun postDeathSaveSuccess(id: Int, sign: Int)
    fun postDeathSaveFailures(id: Int, sign: Int)
    fun postSpellSlots(spellSlots: List<Resource>, id: Int)
    fun deleteCharacterClassSpellCrossRefs(classId: Int, characterId: Int)
    fun postCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean)
    fun deleteFeatureFeatureChoice(choiceId: Int, characterId: Int)
}