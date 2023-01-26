package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.*
import gmail.loganchazdon.dndhelper.model.database.daos.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val characterDao: CharacterDao,
    private val raceDao: RaceDao,
    private val backgroundDao : BackgroundDao,
    private val classDao: ClassDao,
    private val featureDao: FeatureDao
) {
    val scope = CoroutineScope(Job())

    fun getAllCharacters(): LiveData<List<Character>> {
        return characterDao.getAllCharacters()
    }

    fun insertCharacter(character: CharacterEntity) {
        characterDao.insertCharacter(character)
    }

    fun deleteCharacterById(id: Int) {
        scope.launch {
            characterDao.deleteCharacter(id)
        }
    }

    fun createDefaultCharacter(): Int {
        val newCharacter = Character(name = "My Character")
        return characterDao.insertCharacter(newCharacter).toInt()
    }

    fun insertCharacterSubraceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef) {
        characterDao.insertCharacterSubRaceCrossRef(characterSubraceCrossRef)
    }

    fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        characterDao.insertSubraceChoiceEntity(subraceChoiceEntity)
    }

    fun insertCharacterSubclassCrossRef(characterSubclassCrossRef: CharacterSubclassCrossRef) {
        characterDao.insertCharacterSubclassCrossRef(
            characterSubclassCrossRef
        )
    }

    fun insertFeatureChoiceChoiceEntity(featureChoiceChoiceEntity: FeatureChoiceChoiceEntity) {
        characterDao.insertFeatureChoiceEntity(featureChoiceChoiceEntity)
    }

    fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        prepared: Boolean?
    ) {
        characterDao.insertCharacterClassSpellCrossRef(
            CharacterClassSpellCrossRef(
                classId = classId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = prepared
            )
        )
    }

    fun insertSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        characterDao.insertSubClassSpellCastingCrossRef(
            SubclassSpellCastingSpellCrossRef(
                subclassId = subclassId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = isPrepared
            )
        )
    }

    fun insertCharacterClassEquipment(
        equipmentChoices: List<ItemChoice>,
        equipment: List<ItemInterface>,
        characterId: Int
    ) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        if (backpack.classItems.isEmpty()) {
            backpack.classItems.addAll(equipment)
            equipmentChoices.forEach {
                backpack.classItems.addAll(it.chosen?.flatten() ?: emptyList())
            }
        }

        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    fun removeClassFromCharacter(classId: Int, characterId: Int) {
        characterDao.removeCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        characterDao.insertCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        characterDao.insertClassChoiceEntity(classChoiceEntity)
    }

    fun addFeatsToCharacterClass(characterId: Int, classId: Int, feats: List<Feat>) {
        feats.forEach {
            characterDao.insertCharacterClassFeatCrossRef(
                ClassFeatCrossRef(
                    characterId = characterId,
                    featId = it.id,
                    classId = classId
                )
            )
        }
    }

    fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        characterDao.insertBackgroundChoiceEntity(backgroundChoiceEntity)
    }

    fun insertRaceChoiceEntity(raceChoiceEntity: RaceChoiceEntity) {
        characterDao.insertRaceChoice(raceChoiceEntity)
    }

    fun insertCharacterRaceCrossRef(characterRaceCrossRef: CharacterRaceCrossRef) {
        characterDao.insertCharacterRaceCrossRef(characterRaceCrossRef)
    }

    fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        characterDao.insertCharacterBackgroundCrossRef(
            CharacterBackgroundCrossRef(
                backgroundId = backgroundId,
                characterId = characterId
            )
        )
    }

    /**Returns a list of booleans to spells
    If the boolean is null the spell does not require preparation.
    Else the boolean represents whether or not the spell is prepared.*/
    fun getSpellsForCharacter(character: Character): MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        character.classes.forEach {
            addSpellsFromSpellCasting(character.id, it.value.spellCasting, listOf(it.value.name), spells)
            it.value.subclass?.spellCasting?.let { spellCasting ->
                addSpellsFromSpellCasting(character.id, spellCasting, spellCasting.learnFrom, spells)
            }
        }

        character.additionalSpells.forEach {
            spells[it.key]?.addAll(it.value)
        }

        character.classes.forEach { clazz ->
            clazz.value.pactMagic?.known?.let { known ->
                known.forEach {
                    if (spells.getOrDefault(it.level, null) == null) {
                        spells[it.level] = mutableListOf()
                    }
                    spells[it.level]?.add(Pair(null, it))
                }
            }
        }
        return spells
    }

    private fun addSpellsFromSpellCasting(
        id: Int,
        spellCasting: SpellCasting?,
        lists: List<String>?,
        spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>>
    ) {
        when(spellCasting?.prepareFrom) {
            null -> {
                //Non preparation casters
                spellCasting?.known?.forEach { spell ->
                    spells[spell.first.level]?.add(Pair(first = null, second = spell.first))
                }
            }
            "all" -> {
                //Spell casters that prepare from all of their respective class spells
                spellCasting.known.forEach { spell ->
                    if(spell.first.level == 0) {
                        if (spells.getOrDefault(spell.first.level, null) == null) {
                            spells[spell.first.level] = mutableListOf()
                        }
                        spells[spell.first.level]?.add(Pair(first = null, second = spell.first))
                    }
                }

                val listsToCheck = mutableListOf<String>()

                lists?.let {
                    listsToCheck.addAll(it)
                }
                spellCasting.learnFrom?.let {
                    listsToCheck.addAll(it)
                }

                listsToCheck.forEach {
                    characterDao.getAllSpellsByList(
                        id,
                        getClassIdsByName(it)
                    ).forEach { (spell, prepared) ->
                        if (spell.level != 0) {
                            if (spells.getOrDefault(spell.level, null) == null) {
                                spells[spell.level] = mutableListOf()
                            }
                            spells[spell.level]?.add(Pair(prepared, spell))
                        }
                    }
                }
            }
            "known" -> {
                //Spell casters that prepare from known spells
                spellCasting.known.forEach { spell ->
                    if(spells.getOrDefault(spell.first.level, null) == null) {
                        spells[spell.first.level] = mutableListOf()
                    }
                    spells[spell.first.level]?.add(Pair(spell.second, spell.first))
                }
            }
        }
    }

    private fun getClassIdsByName(name: String): List<Int> {
        return classDao.getClassIdsByName(name)
    }

    private fun fillOutFeatureList(features: List<Feature>, characterId: Int) {
        features.forEach { feature ->
            feature.choices =
                fillOutChoices(featureDao.getFeatureChoices(feature.featureId), characterId = characterId)
            feature.spells = featureDao.getFeatureSpells(feature.featureId)
        }
    }

    /**
    This only fills out chosen not options.
    We don't want options as it is not used inside of the character object.
    */
    private fun fillOutChoices(
        choiceEntities: List<FeatureChoiceEntity>,
        characterId: Int
    ): List<FeatureChoice> {
        val choices = mutableListOf<FeatureChoice>()
        choiceEntities.forEach { featureChoiceEntity ->
            val features =
                characterDao.getFeatureChoiceChosen(choiceId = featureChoiceEntity.id, characterId = characterId)
            fillOutFeatureList(features, characterId)
            choices.add(
                FeatureChoice(
                    entity = featureChoiceEntity,
                    options = emptyList(),
                    chosen = features
                )
            )
        }
        return choices
    }

    fun getCharacterById(id: Int): Character {
        val character = characterDao.findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    fun getLiveCharacterById(id: Int, character: MediatorLiveData<Character>) {
        val characterLiveData = characterDao.findLiveCharacterWithoutListChoices(id)
        character.addSource(characterLiveData) {
            if (it != null) {
                scope.launch {
                    fillOutCharacterChoiceLists(it)
                    character.postValue(it)
                }
            }
        }
    }

    /**
    This method is used to fill out choices that require lists. It cannot be done in SQL due to lack of support.
    Note that there is no need to set non-list choices.
     */
    private fun fillOutCharacterChoiceLists(character: Character) {
        //Fill out race choices
        character.race?.let { race ->
            characterDao.getRaceChoiceData(raceId = race.raceId, charId = character.id).let { data ->
                race.proficiencyChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.proficiencyChoice.getOrNull(index) ?: emptyList()
                }

                race.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = raceDao.getRaceFeatures(race.raceId)
                fillOutFeatureList(features, character.id)
                race.traits = features
            }
        }

        character.race?.subrace?.let { subrace ->
            characterDao.getSubraceChoiceData(subraceId = subrace.id, charId = character.id).let { data ->
                subrace.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = raceDao.getSubraceFeatures(subrace.id)
                fillOutFeatureList(features, character.id)
                subrace.traits = features

                val featChoiceEntities = raceDao.getSubraceFeatChoices(subrace.id)
                val featChoices = mutableListOf<FeatChoice>()
                featChoiceEntities.forEach {
                    featChoices.add(
                        it.toFeatChoice(
                            characterDao.getFeatChoiceChosen(
                                characterId = character.id,
                                choiceId = it.id
                            )
                        )
                    )
                }
                subrace.featChoices = featChoices
            }
        }

        character.background?.let { background ->
            characterDao.getBackgroundChoiceData(charId = character.id).let { data ->
                val features = backgroundDao.getBackgroundFeatures(background.id)
                fillOutFeatureList(features, character.id)
                background.features = features

                background.spells = backgroundDao.getBackgroundSpells(backgroundId = background.id)
            }
        }

        val classes = characterDao.getCharactersClasses(character.id)
        classes.forEach { (_, clazz) ->
            val data = characterDao.getClassChoiceData(characterId = character.id, classId = clazz.id)
            clazz.level = data.level
            clazz.abilityImprovementsGranted = data.abilityImprovementsGranted
            clazz.totalNumOnGoldDie = data.totalNumOnGoldDie
            clazz.tookGold = data.tookGold
            clazz.isBaseClass = data.isBaseClass
            clazz.spellCasting?.known =
                characterDao.getSpellCastingSpellsForClass(
                    characterId = character.id,
                    classId = clazz.id
                ).toList()

            val features = characterDao.getClassFeatures(classId = clazz.id, maxLevel = clazz.level)
            fillOutFeatureList(features, character.id)
            clazz.levelPath = features
            clazz.featsGranted = characterDao.getClassFeats(classId = clazz.id, characterId = character.id)
            clazz.featsGranted?.forEach {
                it.features?.let { it1 -> fillOutFeatureList(it1, character.id) }
            }

            clazz.subclass?.let { subclass ->
                subclass.spellCasting?.known =
                    characterDao.getSpellCastingSpellsForSubclass(
                        characterId = character.id,
                        subclassId = subclass.subclassId
                    ).toList()

                val subClassFeatures = classDao.getClassFeatures(classId = clazz.id, maxLevel = clazz.level)
                fillOutFeatureList(subClassFeatures, character.id)
                subclass.features = subClassFeatures
            }

            if (data.isBaseClass) {
                clazz.proficiencyChoices.forEachIndexed { i, it ->
                    it.chosenByString = data.proficiencyChoicesByString[i]
                }
            }
        }

        character.classes = classes
    }

    fun setTemp(id: Int?, temp: String) {
        try {
            characterDao.setTemp(id!!, temp.toInt())
        } catch(_: Exception) { }
    }

    fun heal(id: Int?, hp: String) {
        try {
            characterDao.heal(id!!, hp.toInt())
        } catch(_: Exception) { }
    }

    fun setHp(id: Int?, hp: String) {
        try {
            characterDao.setHp(id!!, hp.toInt())
        } catch(_: Exception) { }
    }

    fun damage(id: Int?, damage: String) {
        try {
            characterDao.damage(id!!, damage.toInt())
        } catch(_: Exception) { }
    }

    fun updateDeathSaveSuccesses(id: Int?, it: Boolean) {
        if(it) {
            characterDao.updateDeathSaveSuccesses(id!!, 1)
        } else {
            characterDao.updateDeathSaveSuccesses(id!!, -1)
        }
    }

    fun updateDeathSaveFailures(id: Int?, it: Boolean) {
        if(it) {
            characterDao.updateDeathSaveFailures(id!!, 1)
        } else {
            characterDao.updateDeathSaveFailures(id!!, -1)
        }
    }

    companion object {
        val statNames = listOf(
            "Strength",
            "Dexterity",
            "Constitution",
            "Intelligence",
            "Wisdom",
            "Charisma"
        )

        val shortStatNames = statNames.onEach {
            it.subSequence(0, 2)
        }
    }
}