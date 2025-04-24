package model.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity
import model.database.daos.*


class CharacterRepository {
    private val characterDao: CharacterDao
    private val raceDao: RaceDao
    private val backgroundDao: BackgroundDao
    private val classDao: ClassDao
    private val subclassDao: SubclassDao
    private val featureDao: FeatureDao

    constructor(
        characterDao: CharacterDao,
        raceDao: RaceDao,
        backgroundDao: BackgroundDao,
        classDao: ClassDao,
        subclassDao: SubclassDao,
        featureDao: FeatureDao
    ) {
        this.characterDao = characterDao
        this.raceDao = raceDao
        this.backgroundDao = backgroundDao
        this.classDao = classDao
        this.subclassDao = subclassDao
        this.featureDao = featureDao
        this.scope = CoroutineScope(Job())
    }

    val scope: CoroutineScope

    suspend fun insertPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int) {
        characterDao.insertPactMagicStateEntity(
            characterId = characterId,
            classId = classId,
            slotsCurrentAmount = slotsCurrentAmount
        )
    }

    fun getAllCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters()
    }

    suspend fun removeFeatureChoiceChoiceEntity(
        choiceId: Int,
        characterId: Int
    ) {
        featureDao.removeFeatureFeatureChoice(choiceId, characterId)
    }

    suspend fun insertCharacter(character: CharacterEntity) {
        if (characterDao.insertCharacter(character).toInt() == -1) {
            characterDao.updateCharacter(character)
        }
    }

    fun deleteCharacterById(id: Int) {
        scope.launch {
            characterDao.deleteCharacter(id)
        }
    }

    suspend fun createDefaultCharacter(): Int {
        val newCharacter = Character(name = "My Character")
        return characterDao.insertCharacter(newCharacter).toInt()
    }

    suspend fun insertCharacterSubraceCrossRef(characterId: Int, subraceId: Int) {
        characterDao.insertCharacterSubRaceCrossRef(
            characterId, subraceId
        )
    }

    suspend fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        characterDao.insertSubraceChoiceEntity(subraceChoiceEntity)
    }

    suspend fun insertCharacterSubclassCrossRef(characterId: Int, subclassId: Int, classId: Int) {
        characterDao.insertCharacterSubclassCrossRef(
            subclassId,
            characterId,
            classId,
        )
    }

    suspend fun insertFeatureChoiceChoiceEntity(
        featureId: Int,
        characterId: Int,
        choiceId: Int
    ) {
        characterDao.insertFeatureChoiceEntity(
            featureId = featureId,
            characterId = characterId,
            choiceId = choiceId
        )
    }

    suspend fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        prepared: Boolean?
    ) {
        characterDao.insertCharacterClassSpellCrossRef(
            classId = classId,
            spellId = spellId,
            characterId = characterId,
            isPrepared = prepared
        )
    }

    suspend fun insertSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        characterDao.insertSubClassSpellCastingCrossRef(
            subclassId = subclassId,
            spellId = spellId,
            characterId = characterId,
            isPrepared = isPrepared
        )
    }

    suspend fun insertCharacterClassEquipment(
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

    suspend fun setClassGold(gold: Int, characterId: Int) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        backpack.classCurrency["gp"]!!.amount = gold
        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    suspend fun setBackgroundCurrency(backgroundCurrencyMap: Map<String, Currency>, characterId: Int) {
        val backpack = characterDao.getCharacterBackPack(characterId)
        backpack.backgroundCurrency = backgroundCurrencyMap
        characterDao.insertCharacterBackPack(backpack, characterId)
    }

    suspend fun removeClassFromCharacter(classId: Int, characterId: Int) {
        characterDao.removeCharacterClassCrossRef(
            characterId = characterId,
            classId = classId
        )
    }

    suspend fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        characterDao.insertCharacterClassCrossRef(
            characterId = characterId,
            classId = classId
        )
    }

    suspend fun insertClassChoiceEntity(
        characterId: Int,
        classId: Int,
        level: Int,
        isBaseClass: Boolean,
        totalNumOnGoldDie: Int?,
        abilityImprovementsGranted: MutableList<Map<String, Int>>,
        tookGold: Boolean,
        proficiencyChoicesByString: List<List<String>>,
    ) {
        characterDao.insertClassChoiceEntity(
            ClassChoiceEntity(
                characterId = characterId,
                classId = classId,
                level = level,
                isBaseClass = isBaseClass,
                totalNumOnGoldDie = totalNumOnGoldDie,
                abilityImprovementsGranted = abilityImprovementsGranted,
                tookGold = tookGold,
                proficiencyChoicesByString = proficiencyChoicesByString
            )
        )
    }

    suspend fun addFeatsToCharacterClass(characterId: Int, classId: Int, feats: List<Feat>) {
        feats.forEach {
            characterDao.insertCharacterClassFeatCrossRef(
                characterId = characterId,
                featId = it.id,
                classId = classId
            )
        }
    }

    suspend fun insertBackgroundChoiceEntity(
        characterId: Int,
        backgroundId: Int,
        languageChoices: List<List<String>>
    ) {
        characterDao.insertBackgroundChoiceEntity(
            BackgroundChoiceEntity(
                characterId,
                backgroundId,
                languageChoices
            )
        )
    }

    suspend fun insertRaceChoiceEntity(
        raceId: Int,
        characterId: Int,
        abilityBonusChoice: List<String>,
        proficiencyChoice: List<List<String>>,
        languageChoice: List<List<String>>,
        abilityBonusOverrides: List<AbilityBonus>?
    ) {
        characterDao.insertRaceChoice(
            RaceChoiceEntity(
                raceId,
                characterId,
                abilityBonusChoice,
                proficiencyChoice,
                languageChoice,
                abilityBonusOverrides
            )
        )
    }

    suspend fun insertCharacterRaceCrossRef(characterId: Int, raceId: Int) {
        characterDao.insertCharacterRaceCrossRef(
            id = characterId,
            raceId = raceId
        )
    }

    suspend fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        characterDao.insertCharacterBackgroundCrossRef(
            backgroundId = backgroundId,
            characterId = characterId
        )
    }

    /**Returns a list of booleans to spells
    If the boolean is null the spell does not require preparation.
    Else the boolean represents whether or not the spell is prepared.*/
    suspend fun getSpellsForCharacter(character: Character): MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> {
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
                    if (spells.getOrElse(it.level, { null }) == null) {
                        spells[it.level] = mutableListOf()
                    }
                    spells[it.level]?.add(Pair(null, it))
                }
            }
        }
        return spells
    }

    private suspend fun addSpellsFromSpellCasting(
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
                    if (spells.getOrElse(spell.first.level, { null }) == null) {
                        spells[spell.first.level] = mutableListOf()
                    }
                    spells[spell.first.level]?.add(Pair(first = null, second = spell.first))
                }
            }

            "all" -> {
                //Spell casters that prepare from all of their respective class spells
                spellCasting.known.filter { it.first.level == 0 }.forEach { spell ->
                    if (spells.getOrElse(spell.first.level, { null }) == null) {
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
                            if (spells.getOrElse(spell.level, { null }) == null) {
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
                    if (spells.getOrElse(spell.first.level, { null }) == null) {
                        spells[spell.first.level] = mutableListOf()
                    }
                    spells[spell.first.level]?.add(Pair(spell.second, spell.first))
                }
            }
        }
    }

    private suspend fun getClassIdsByName(name: String): List<Int> {
        return classDao.getClassIdsByName(name)
    }

    private suspend fun fillOutFeatureList(features: List<Feature>, characterId: Int) {
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
    private suspend fun fillOutChoices(
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

    suspend fun getCharacterById(id: Int): Character {
        val character = characterDao.findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    fun getLiveCharacterById(
        id: Int,
        character: MutableStateFlow<Character>,
        characterKey: Flow<Int>? = null
    ) {
        val characterLiveData = characterDao.findLiveCharacterWithoutListChoices(id)
        val calculate : suspend (it: Character?) -> Unit= {
            if (it != null) {
                this.fillOutCharacterChoiceLists(it)
                character.value = it
            }
        }

        GlobalScope.launch {
            characterLiveData.collect {
                calculate(it)
            }

            //If the key is changed force recalculation.
            //This is needed because not all the tables used in fetching a character are
            //accessed in the initial sql query meaning room will not automatically update live data.
            characterKey?.let { key ->
                key.collect {
                    calculate(characterLiveData.first())
                }
            }
        }
    }

    /**
    This method is used to fill out choices that require lists. It cannot be done in SQL due to lack of support.
    Note that there is no need to set non-list choices.
     */
    @Suppress("SAFE_CALL_WILL_CHANGE_NULLABILITY", "UNNECESSARY_SAFE_CALL")
    //The safe call is ly necessary here because of how room handles null values
    private suspend fun fillOutCharacterChoiceLists(character: Character) {
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

    suspend fun setTemp(id: Int?, temp: String) {
        try {
            characterDao.setTemp(id!!, temp.toInt())
        } catch (_: Exception) {
        }
    }

    suspend fun heal(id: Int?, hp: String, maxHp: Int) {
        try {
            characterDao.heal(id!!, hp.toInt(), maxHp)
        } catch (_: Exception) {
        }
    }

    suspend fun setHp(id: Int?, hp: String) {
        try {
            characterDao.setHp(id!!, hp.toInt())
        } catch (_: Exception) {
        }
    }

    suspend fun damage(id: Int?, damage: String) {
        try {
            characterDao.damage(id!!, damage.toInt())
        } catch (_: Exception) {
        }
    }

    suspend fun updateDeathSaveSuccesses(id: Int?, it: Boolean) {
        if (it) {
            characterDao.updateDeathSaveSuccesses(id!!, 1)
        } else {
            characterDao.updateDeathSaveSuccesses(id!!, -1)
        }
    }

    suspend fun updateDeathSaveFailures(id: Int?, it: Boolean) {
        if (it) {
            characterDao.updateDeathSaveFailures(id!!, 1)
        } else {
            characterDao.updateDeathSaveFailures(id!!, -1)
        }
    }

    suspend fun insertSpellSlots(spellSlots: List<Resource>, id: Int) {
        characterDao.insertSpellSlots(spellSlots, id)
    }

    suspend fun removeClassSpellCrossRefs(classId: Int, characterId: Int) {
        characterDao.removeCharacterClassSpellCrossRefs(classId, characterId)
    }

    suspend fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int {
        return characterDao.getNumOfPreparedSpells(classId = classId, characterId = characterId)
    }

    suspend fun changeName(it: String, id: Int) {
        characterDao.changeName(it, id)
    }

    suspend fun setPersonalityTraits(it: String, id: Int) {
        characterDao.setPersonalityTraits(it, id)
    }

    suspend fun setIdeals(it: String, id: Int) {
        characterDao.setIdeals(it, id)
    }

    suspend fun setBonds(it: String, id: Int) {
        characterDao.setBonds(it, id)
    }

    suspend fun setFlaws(it: String, id: Int) {
        characterDao.setFlaws(it, id)
    }

    suspend fun setNotes(it: String, id: Int) {
        characterDao.setNotes(it, id)
    }

    suspend fun activateInfusion(infusionId: Int, characterId: Int) {
        characterDao.insertCharacterFeatureState(
            featureId = infusionId,
            characterId = characterId,
            isActive = true
        )
    }

    suspend fun deactivateInfusion(infusionId: Int, characterId: Int) {
        characterDao.insertCharacterFeatureState(
            featureId = infusionId,
            characterId = characterId,
            isActive = false
        )
    }

    suspend fun removeFeatureChoiceCrossRefs(clazz: Class, characterId: Int) {
        clazz.levelPath?.forEach { feature ->
            feature.choices?.forEach {
                featureDao.removeFeatureFeatureChoice(
                    choiceId = it.id,
                    characterId = characterId
                )
            }
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

        val shortStatNames = statNames.map {
            it.take(3)
        }

    }
}