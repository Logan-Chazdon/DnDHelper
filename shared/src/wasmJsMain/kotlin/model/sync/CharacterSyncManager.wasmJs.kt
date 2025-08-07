package model.sync

import model.Backpack
import model.CharacterEntity
import model.Resource
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity


/**
 * This object does nothing as the web platform does not need syncing.
*/
actual class CharacterSyncManager {
    actual fun postCharacter(character: CharacterEntity) {
    }

    actual fun deleteCharacter(id: Int) {
    }

    actual fun updateCharacterName(id: Int, name: String) {
    }

    actual fun updateCharacterTraits(id: Int, name: String) {
    }

    actual fun updateCharacterIdeals(id: Int, name: String) {
    }

    actual fun updateCharacterBonds(id: Int, name: String) {
    }

    actual fun updateCharacterFlaws(id: Int, name: String) {
    }

    actual fun updateCharacterNotes(id: Int, name: String) {
    }

    actual fun postPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int) {
    }

    actual fun postCharacterSubraceCrossRef(characterId: Int, subraceId: Int) {
    }

    actual fun postSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
    }

    actual fun postCharacterSubclassCrossRef(subclassId: Int, characterId: Int, classId: Int) {
    }

    actual fun postFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
    }

    actual fun postCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
    }

    actual fun postSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
    }

    actual fun postCharacterBackPack(backpack: Backpack, characterId: Int) {
    }

    actual fun deleteClassCharacterCrossRef(characterId: Int, classId: Int) {
    }

    actual fun postCharacterClassCrossRef(characterId: Int, classId: Int) {
    }

    actual fun postClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
    }

    actual fun postCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
    }

    actual fun postBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
    }

    actual fun postRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
    }

    actual fun postCharacterRaceCrossRef(id: Int, raceId: Int) {
    }

    actual fun postCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
    }

    actual fun postTemp(id: Int?, temp: String) {
    }

    actual fun postHeal(id: Int?, hp: String, maxHp: Int) {
    }

    actual fun postHp(id: Int, toInt: Int) {
    }

    actual fun postDamage(id: Int, toInt: Int) {
    }

    actual fun postDeathSaveSuccess(id: Int, sign: Int) {
    }

    actual fun postDeathSaveFailures(id: Int, sign: Int) {
    }

    actual fun postSpellSlots(spellSlots: List<Resource>, id: Int) {
    }

    actual fun deleteCharacterClassSpellCrossRefs(classId: Int, characterId: Int) {
    }

    actual fun postCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean) {
    }

    actual fun deleteFeatureFeatureChoice(choiceId: Int, characterId: Int) {
    }
}