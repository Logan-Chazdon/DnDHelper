package model.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.*
import model.choiceEntities.*
import model.database.daos.*
import model.junctionEntities.*
import model.repositories.SpellRepository.Companion.allSpellLevels
import model.stateEntities.CharacterFeatureState
import model.stateEntities.PactMagicStateEntity


actual class CharacterRepository(
    private val characterDao: CharacterDao,
    private val raceDao: RaceDao,
    private val backgroundDao: BackgroundDao,
    private val classDao: ClassDao,
    private val subclassDao: SubclassDao,
    private val featureDao: FeatureDao
) {
    val scope = CoroutineScope(Job())

    actual fun insertPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int) {
        characterDao.insertPactMagicStateEntity(
            PactMagicStateEntity(
                characterId = characterId,
                classId = classId,
                slotsCurrentAmount = slotsCurrentAmount
            )
        )
    }

    actual fun getAllCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters().asFlow()
    }

    actual fun removeFeatureChoiceChoiceEntity(
        choiceId: Int,
        characterId: Int
    ) {
        featureDao.removeFeatureFeatureChoice(choiceId, characterId)
    }

    actual fun insertCharacter(character: CharacterEntity) {
        if (characterDao.insertCharacter(character).toInt() == -1) {
            characterDao.updateCharacter(character)
        }
    }

    actual fun deleteCharacterById(id: Int) {
        scope.launch {
            characterDao.deleteCharacter(id)
        }
    }

    actual fun createDefaultCharacter(): Int {
        val newCharacter = Character(name = "My Character")
        return characterDao.insertCharacter(newCharacter).toInt()
    }

    actual fun insertCharacterSubraceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef) {
        characterDao.insertCharacterSubRaceCrossRef(characterSubraceCrossRef)
    }

    actual fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        characterDao.insertSubraceChoiceEntity(subraceChoiceEntity)
    }

    actual fun insertCharacterSubclassCrossRef(characterSubclassCrossRef: CharacterSubclassCrossRef) {
        characterDao.insertCharacterSubclassCrossRef(
            characterSubclassCrossRef
        )
    }

    actual fun insertFeatureChoiceChoiceEntity(featureChoiceChoiceEntity: FeatureChoiceChoiceEntity) {
        characterDao.insertFeatureChoiceEntity(featureChoiceChoiceEntity)
    }

    actual fun insertCharacterClassSpellCrossRef(
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

    actual fun insertSubclassSpellCastingSpellCrossRef(
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

    actual fun insertCharacterClassEquipment(
        equipmentChoices: List<ItemChoice>,
        equipment: List<ItemInterface>,
        characterId: Int
    ) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        if (backpack.classItems.isEmpty()) {
            backpack.addClassItems(equipment)
            equipmentChoices.forEach {
                backpack.addClassItems(it.chosen?.flatten() ?: emptyList())
            }
        }

        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    actual fun setClassGold(gold: Int, characterId: Int) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        backpack.classCurrency["gp"]!!.amount = gold
        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    actual fun setBackgroundCurrency(backgroundCurrencyMap: Map<String, Currency>, characterId: Int) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        backpack.backgroundCurrency = backgroundCurrencyMap
        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    actual fun removeClassFromCharacter(classId: Int, characterId: Int) {
        characterDao.removeCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    actual fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        characterDao.insertCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    actual fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        characterDao.insertClassChoiceEntity(classChoiceEntity)
    }

    actual fun addFeatsToCharacterClass(characterId: Int, classId: Int, feats: List<Feat>) {
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

    actual fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        characterDao.insertBackgroundChoiceEntity(backgroundChoiceEntity)
    }

    actual fun insertRaceChoiceEntity(raceChoiceEntity: RaceChoiceEntity) {
        characterDao.insertRaceChoice(raceChoiceEntity)
    }

    actual fun insertCharacterRaceCrossRef(characterRaceCrossRef: CharacterRaceCrossRef) {
        characterDao.insertCharacterRaceCrossRef(characterRaceCrossRef)
    }

    actual fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
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
    actual fun getSpellsForCharacter(character: Character): MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        character.classes.forEach {
            addSpellsFromSpellCasting(
                character.id,
                it.value.spellCasting,
                listOf(it.value.name),
                spells,
                it.value.level
            )
            it.value.subclass?.spellCasting?.let { spellCasting ->
                addSpellsFromSpellCasting(
                    character.id,
                    spellCasting,
                    spellCasting.learnFrom,
                    spells,
                    it.value.level
                )
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
        spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>>,
        level: Int
    ) {
        when (spellCasting?.prepareFrom) {
            null -> {
                //Non preparation casters
                spellCasting?.known?.forEach { spell ->
                    if (spells.getOrDefault(spell.first.level, null) == null) {
                        spells[spell.first.level] = mutableListOf()
                    }
                    spells[spell.first.level]?.add(Pair(first = null, second = spell.first))
                }
            }

            "all" -> {
                //Spell casters that prepare from all of their respective class spells
                spellCasting.known.filter { it.first.level == 0 }.forEach { spell ->
                    if (spells.getOrDefault(spell.first.level, null) == null) {
                        spells[spell.first.level] = mutableListOf()
                    }
                    spells[spell.first.level]?.add(Pair(first = null, second = spell.first))
                }

                val listsToCheck = mutableListOf<String>()

                lists?.let {
                    listsToCheck.addAll(it)
                }
                spellCasting.learnFrom?.let {
                    listsToCheck.addAll(it)
                }
                val maxSpellLevel = allSpellLevels.first {
                    spellCasting.spellSlotsByLevel?.get(level)?.last()?.name == it.second
                }.first

                listsToCheck.forEach {
                    characterDao.getAllSpellsByList(
                        id,
                        getClassIdsByName(it)
                    ).forEach { (spell, prepared) ->
                        if (spell.level != 0) {
                            if (spells.getOrDefault(spell.level, null) == null) {
                                spells[spell.level] = mutableListOf()
                            }
                            if (spell.level <= maxSpellLevel) {
                                spells[spell.level]?.add(Pair(prepared ?: false, spell))
                            }
                        }
                    }
                }
            }

            "known" -> {
                //Spell casters that prepare from known spells
                spellCasting.known.forEach { spell ->
                    if (spells.getOrDefault(spell.first.level, null) == null) {
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
                fillOutChoices(
                    featureDao.getFeatureChoices(feature.featureId),
                    characterId = characterId
                )
            feature.spells = featureDao.getFeatureSpells(feature.featureId)
            feature.infusion?.active = characterDao.isFeatureActive(
                featureId = feature.featureId,
                characterId = characterId
            )
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
                characterDao.getFeatureChoiceChosen(
                    choiceId = featureChoiceEntity.id,
                    characterId = characterId
                )
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

    actual fun getCharacterById(id: Int): Character {
        val character = characterDao.findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    //TODO this is changed a lot due to using kotlin flows. Test extensively.
    //TODO This almost definitely non functional.
    actual fun getLiveCharacterById(
        id: Int,
        character: MutableStateFlow<Character>,
        characterKey: Flow<Int>? //= null
    ) {
        val characterLiveData = characterDao.findLiveCharacterWithoutListChoices(id)
        val calculate = fun(it: Character?) {
            if (it != null) {
                scope.launch {
                    fillOutCharacterChoiceLists(it)
                    character.value = it
                }
            }
        }


        (character.asLiveData() as MediatorLiveData<Character>).addSource(characterLiveData) {
            calculate(it)
        }

        //If the key is changed force recalculation.
        //This is needed because not all the tables used in fetching a character are
        //accessed in the initial sql query meaning room will not automatically update live data.
        characterKey?.let { key ->
            (character.asLiveData() as MediatorLiveData<Character>).addSource((key.asLiveData() as MediatorLiveData<*>)) {
                calculate(characterLiveData.value)
            }
        }
    }

    /**
    This method is used to fill out choices that require lists. It cannot be done in SQL due to lack of support.
    Note that there is no need to set non-list choices.
     */
    @Suppress("SAFE_CALL_WILL_CHANGE_NULLABILITY", "UNNECESSARY_SAFE_CALL")
    //The safe call is actually necessary here because of how room handles null values
    private fun fillOutCharacterChoiceLists(character: Character) {
        //Fill out race choices
        character.race?.let { race ->
            characterDao.getRaceChoiceData(raceId = race.raceId, charId = character.id)
                ?.let { data ->
                    race.proficiencyChoices.forEachIndexed { index, choice ->
                        choice.chosenByString =
                            data.proficiencyChoice.getOrNull(index) ?: emptyList()
                    }

                    race.languageChoices.forEachIndexed { index, choice ->
                        choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                    }

                    data.abilityBonusOverrides?.let {
                        if (it.isNotEmpty()) {
                            race.abilityBonuses = it
                        }
                    }

                    val features = raceDao.getRaceFeatures(race.raceId)
                    fillOutFeatureList(features, character.id)
                    race.traits = features
                }
        }

        character.race?.subrace?.let { subrace ->
            characterDao.getSubraceChoiceData(subraceId = subrace.id, charId = character.id)
                ?.let { data ->
                    subrace.languageChoices.forEachIndexed { index, choice ->
                        choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                    }

                    subrace.abilityBonusChoice?.chosenByString = data.abilityBonusChoice

                    data.abilityBonusOverrides?.let {
                        if (it.isNotEmpty()) {
                            subrace.abilityBonuses = it
                        }
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
                                ),
                                emptyList()
                            )
                        )
                    }
                    subrace.featChoices = featChoices
                }
        }

        character.background?.let { background ->
            characterDao.getBackgroundChoiceData(charId = character.id)?.let { data ->
                val features = backgroundDao.getBackgroundFeatures(background.id)
                fillOutFeatureList(features, character.id)
                background.features = features

                background.spells = backgroundDao.getBackgroundSpells(backgroundId = background.id)
            }
        }

        val classes = characterDao.getCharactersClasses(character.id)
        classes.forEach { (_, clazz) ->
            val data =
                characterDao.getClassChoiceData(characterId = character.id, classId = clazz.id)
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
            clazz.pactMagic?.pactSlots?.get(clazz.level - 1)?.currentAmount =
                characterDao.getCharacterPactSlots(characterId = character.id, classId = clazz.id)
            clazz.pactMagic?.known = characterDao.getPactMagicSpells(
                characterId = character.id,
                classId = clazz.id
            )

            val features = characterDao.getClassFeatures(classId = clazz.id, maxLevel = clazz.level)
            fillOutFeatureList(features, character.id)
            clazz.levelPath = features
            clazz.featsGranted =
                characterDao.getClassFeats(classId = clazz.id, characterId = character.id)
            clazz.featsGranted?.forEach {
                it.features?.let { it1 -> fillOutFeatureList(it1, character.id) }
            }

            clazz.subclass?.let { subclass ->
                subclass.spellCasting?.known =
                    characterDao.getSpellCastingSpellsForSubclass(
                        characterId = character.id,
                        subclassId = subclass.subclassId
                    ).toList()

                val subClassFeatures = subclassDao.getSubclassFeatures(
                    subclassId = subclass.subclassId,
                    maxLevel = clazz.level
                )
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

    actual fun setTemp(id: Int?, temp: String) {
        try {
            characterDao.setTemp(id!!, temp.toInt())
        } catch (_: Exception) {
        }
    }

    actual fun heal(id: Int?, hp: String, maxHp: Int) {
        try {
            characterDao.heal(id!!, hp.toInt(), maxHp)
        } catch (_: Exception) {
        }
    }

    actual fun setHp(id: Int?, hp: String) {
        try {
            characterDao.setHp(id!!, hp.toInt())
        } catch (_: Exception) {
        }
    }

    actual fun damage(id: Int?, damage: String) {
        try {
            characterDao.damage(id!!, damage.toInt())
        } catch (_: Exception) {
        }
    }

    actual fun updateDeathSaveSuccesses(id: Int?, it: Boolean) {
        if (it) {
            characterDao.updateDeathSaveSuccesses(id!!, 1)
        } else {
            characterDao.updateDeathSaveSuccesses(id!!, -1)
        }
    }

    actual fun updateDeathSaveFailures(id: Int?, it: Boolean) {
        if (it) {
            characterDao.updateDeathSaveFailures(id!!, 1)
        } else {
            characterDao.updateDeathSaveFailures(id!!, -1)
        }
    }

    actual fun insertSpellSlots(spellSlots: List<Resource>, id: Int) {
        characterDao.insertSpellSlots(spellSlots, id)
    }

    actual fun removeClassSpellCrossRefs(classId: Int, characterId: Int) {
        characterDao.removeCharacterClassSpellCrossRefs(classId, characterId)
    }

    actual fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int {
        return characterDao.getNumOfPreparedSpells(classId = classId, characterId = characterId)
    }

    actual fun changeName(it: String, id: Int) {
        characterDao.changeName(it, id)
    }

    actual fun setPersonalityTraits(it: String, id: Int) {
        characterDao.setPersonalityTraits(it, id)
    }

    actual fun setIdeals(it: String, id: Int) {
        characterDao.setIdeals(it, id)
    }

    actual fun setBonds(it: String, id: Int) {
        characterDao.setBonds(it, id)
    }

    actual fun setFlaws(it: String, id: Int) {
        characterDao.setFlaws(it, id)
    }

    actual fun setNotes(it: String, id: Int) {
        characterDao.setNotes(it, id)
    }

    actual fun activateInfusion(infusionId: Int, characterId: Int) {
        characterDao.insertCharacterFeatureState(
            CharacterFeatureState(
                featureId = infusionId,
                characterId = characterId,
                isActive = true
            )
        )
    }

    actual fun deactivateInfusion(infusionId: Int, characterId: Int) {
        characterDao.insertCharacterFeatureState(
            CharacterFeatureState(
                featureId = infusionId,
                characterId = characterId,
                isActive = false
            )
        )
    }

    actual fun removeFeatureChoiceCrossRefs(clazz: Class, characterId: Int) {
        clazz.levelPath?.forEach { feature ->
            feature.choices?.forEach {
                featureDao.removeFeatureFeatureChoice(
                    choiceId = it.id,
                    characterId = characterId
                )
            }
        }
    }


    actual companion object {
        actual val statNames = listOf(
            "Strength",
            "Dexterity",
            "Constitution",
            "Intelligence",
            "Wisdom",
            "Charisma"
        )

        actual val shortStatNames = statNames.map {
            it.take(3)
        }

    }
}