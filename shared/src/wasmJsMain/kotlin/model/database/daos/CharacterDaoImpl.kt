package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.*
import services.CharacterService

class CharacterDaoImpl(characterService: CharacterService) : CharacterDao(characterService) {
    override suspend fun getSpellCastingSpellsForSubclass(characterId: Int, subclassId: Int): Map<Spell, Boolean?> {
        return characterService.getSpellCastingSpellsForSubclass(characterId, subclassId)
    }

    override suspend fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?> {
        return characterService.getSpellCastingSpellsForClass(characterId, classId)
    }

    override suspend fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat> {
        return characterService.getClassFeats(classId, characterId)
    }


    override suspend fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat> {
        return characterService.getFeatChoiceChosen(characterId, choiceId)
    }

    override suspend fun deleteCharacter(id: Int) {
        characterService.deleteCharacter(id)
    }

    override suspend fun getCharacterBackPack(id: Int): Backpack {
        return characterService.getCharacterBackPack(id)
    }

    override suspend fun insertCharacterBackPack(backpack: Backpack, id: Int) {
       characterService.insertCharacterBackPack(backpack, id)
    }

    override suspend fun insertCharacterRaceCrossRef(id: Int, raceId: Int) {
        characterService.insertCharacterRaceCrossRef(id, raceId)
    }

    override suspend fun getAllSpellsByList(id: Int, classIdsByName: List<Int>): Map<Spell, Boolean?> {
        return characterService.getAllSpellsByList(id, classIdsByName)
    }

    override suspend fun isFeatureActive(featureId: Int, characterId: Int): Boolean {
        return characterService.isFeatureActive(featureId, characterId)
    }

    override suspend fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature> {
        return characterService.getFeatureChoiceChosen(choiceId, characterId)
    }

    override suspend fun findCharacterWithoutListChoices(id: Int): Character {
        return characterService.findCharacterWithoutListChoices(id)
    }

    override fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character> {
        return characterService.findLiveCharacterWithoutListChoices(id)
    }

    override suspend fun getCharacterPactSlots(classId: Int, characterId: Int): Int {
        return characterService.getCharacterPactSlots(classId, characterId)
    }

    override suspend fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell> {
        return characterService.getPactMagicSpells(characterId, classId)
    }

    override suspend fun getClassFeatures(classId: Int, maxLevel: Int): MutableList<Feature> {
        return characterService.getClassFeatures(classId, maxLevel)
    }

    override suspend fun setTemp(id: Int, temp: Int) {
        characterService.setTemp(id, temp)
    }

    override suspend fun heal(id: Int, hp: Int, maxHp: Int) {
        characterService.heal(id, hp, maxHp)
    }

    override suspend fun damage(id: Int, damage: Int) {
        characterService.damage(id, damage)
    }

    override suspend fun setHp(id: Int, hp: Int) {
        characterService.setHp(id, hp)
    }

    override suspend fun updateDeathSaveSuccesses(id: Int, it: Int) {
        characterService.updateDeathSaveSuccesses(id, it)
    }

    override suspend fun updateDeathSaveFailures(id: Int, it: Int) {
        characterService.updateDeathSaveFailures(id, it)
    }

    override suspend fun insertSpellSlots(spellSlots: List<Resource>, id: Int) {
        characterService.insertSpellSlots(spellSlots, id)
    }

    override suspend fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int) {
        characterService.removeCharacterClassSpellCrossRefs(classId, characterId)
    }

    override suspend fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int {
        return characterService.getNumOfPreparedSpells(classId, characterId)
    }

    override suspend fun changeName(it: String, id: Int) {
        characterService.changeName(it, id)
    }

    override suspend fun setPersonalityTraits(it: String, id: Int) {
        characterService.setPersonalityTraits(it, id)
    }

    override suspend fun setIdeals(it: String, id: Int) {
        characterService.setIdeals(it, id)
    }

    override suspend fun setNotes(it: String, id: Int) {
        characterService.setNotes(it, id)
    }

    override suspend fun setFlaws(it: String, id: Int) {
        characterService.setFlaws(it, id)
    }

    override suspend fun setBonds(it: String, id: Int) {
       characterService.setBonds(it, id)
    }
}