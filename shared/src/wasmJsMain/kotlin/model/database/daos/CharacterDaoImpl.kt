package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.*
import services.CharacterService

class CharacterDaoImpl(characterService: CharacterService) : CharacterDao(characterService) {
    override fun getSpellCastingSpellsForSubclass(characterId: Int, subclassId: Int): Map<Spell, Boolean?> {
        TODO("Not yet implemented")
    }

    override fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?> {
        TODO("Not yet implemented")
    }

    override fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat> {
        TODO("Not yet implemented")
    }

    override fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCharacter(id: Int) {
        characterService.deleteCharacter(id)
    }

    override fun getCharacterBackPack(id: Int): Backpack {
        TODO("Not yet implemented")
    }

    override fun insertCharacterBackPack(backpack: Backpack, id: Int) {
        TODO("Not yet implemented")
    }

    override fun insertCharacterRaceCrossRef(id: Int, raceId: Int) {
        TODO("Not yet implemented")
    }

    override fun getAllSpellsByList(id: Int, classIdsByName: List<Int>): Map<Spell, Boolean?> {
        TODO("Not yet implemented")
    }

    override fun isFeatureActive(featureId: Int, characterId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun findCharacterWithoutListChoices(id: Int): Character {
        TODO("Not yet implemented")
    }

    override fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character> {
        TODO("Not yet implemented")
    }

    override fun getCharactersClasses(characterId: Int): MutableMap<String, Class> {
        TODO("Not yet implemented")
    }

    override fun getCharacterPactSlots(classId: Int, characterId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell> {
        TODO("Not yet implemented")
    }

    override fun getClassFeatures(classId: Int, maxLevel: Int): MutableList<Feature> {
        TODO("Not yet implemented")
    }

    override fun setTemp(id: Int, temp: Int) {
        TODO("Not yet implemented")
    }

    override fun heal(id: Int, hp: Int, maxHp: Int) {
        TODO("Not yet implemented")
    }

    override fun damage(id: Int, damage: Int) {
        TODO("Not yet implemented")
    }

    override fun setHp(id: Int, hp: Int) {
        TODO("Not yet implemented")
    }

    override fun updateDeathSaveSuccesses(id: Int, it: Int) {
        TODO("Not yet implemented")
    }

    override fun updateDeathSaveFailures(id: Int, it: Int) {
        TODO("Not yet implemented")
    }

    override fun insertSpellSlots(spellSlots: List<Resource>, id: Int) {
        TODO("Not yet implemented")
    }

    override fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int) {
        TODO("Not yet implemented")
    }

    override fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun changeName(it: String, id: Int) {
        TODO("Not yet implemented")
    }

    override fun setPersonalityTraits(it: String, id: Int) {
        TODO("Not yet implemented")
    }

    override fun setIdeals(it: String, id: Int) {
        TODO("Not yet implemented")
    }

    override fun setNotes(it: String, id: Int) {
        TODO("Not yet implemented")
    }

    override fun setFlaws(it: String, id: Int) {
        TODO("Not yet implemented")
    }

    override fun setBonds(it: String, id: Int) {
        TODO("Not yet implemented")
    }
}