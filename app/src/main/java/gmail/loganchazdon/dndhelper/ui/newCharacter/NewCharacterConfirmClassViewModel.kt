package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.ClassChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterSubclassCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository.Companion.shortStatNames
import gmail.loganchazdon.dndhelper.model.repositories.ClassRepository
import gmail.loganchazdon.dndhelper.model.repositories.FeatRepository
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.utils.getDropDownState
import gmail.loganchazdon.dndhelper.ui.newCharacter.utils.getFeatsAt
import gmail.loganchazdon.dndhelper.ui.utils.allNames
import gmail.loganchazdon.dndhelper.ui.utils.toStringList
import javax.inject.Inject


@HiltViewModel
class NewCharacterConfirmClassViewModel @Inject constructor(
    featRepository: FeatRepository,
    private val characterRepository: CharacterRepository,
    private val classRepository: ClassRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val clazz = classRepository.getClass(savedStateHandle.get<String>("classId")!!.toInt())
    var takeGold = mutableStateOf(false)
    val featureDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    val classSpells = mutableStateListOf<Spell>()
    val subclassSpells = mutableStateListOf<Spell>()
    val featNames: MediatorLiveData<MutableList<String>> = MediatorLiveData()
    val isFeat = mutableStateListOf<Boolean>()
    val featDropDownStates = mutableStateListOf<MultipleChoiceDropdownStateImpl>()
    val featChoiceDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    val absDropDownStates = mutableStateListOf<MultipleChoiceDropdownStateImpl>()
    var goldRolled = mutableStateOf(
        (clazz.value?.startingGoldD4s?.times(2) ?: 4).toString()
    )
    var isBaseClass = mutableStateOf(true)
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    val levels = mutableStateOf(TextFieldValue("1"))
    val feats: LiveData<List<Feat>> = featRepository.getFeats()
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()
    val subclasses =
        classRepository.getSubclassesByClassId(savedStateHandle.get<String>("classId")!!.toInt())

    init {
        featNames.addSource(feats) {
            val names = mutableListOf<String>()
            for (item in it) {
                names.add(item.name)
            }
            featNames.value = names
        }

        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if (id != -1) {
            characterRepository.getLiveCharacterById(
                savedStateHandle.get<String>("characterId")!!.toInt(),
                character
            )
        }
    }

    private fun getFeatures(features: List<Feature>, level: Int): List<Feature> {
        features.filter { it.grantedAtLevel <= level }.forEach { feature ->
            feature.choices?.forEachIndexed { index, it ->
                if (it.choose.num(level) != 0 && it.options?.isEmpty() == false) {
                    it.chosen =
                        featureDropdownStates[index.toString() + feature.name + feature.grantedAtLevel]
                            ?.getSelected()
                }
            }
        }
        return features
    }

    private fun saveFeatures(features: List<Feature>) {
        getFeatures(features, toNumber(levels)).forEach { feature ->
            feature.choices?.forEach { choice ->
                choice.chosen?.forEach { chosen ->
                    characterRepository.insertFeatureChoiceChoiceEntity(
                        FeatureChoiceChoiceEntity(
                            featureId = chosen.featureId,
                            choiceId = choice.id,
                            characterId = id
                        )
                    )
                }
            }
        }
    }

    suspend fun addClassLevels() {
        if (id == -1)
            id = characterRepository.createDefaultCharacter()

        characterRepository.removeFeatureChoiceCrossRefs(
            clazz.value!!,
            id
        )

        characterRepository.insertCharacterClassCrossRef(
            characterId = id,
            classId = clazz.value!!.id
        )
        saveFeatures(clazz.value!!.levelPath!!)

        characterRepository.removeClassSpellCrossRefs(
            clazz.value!!.id,
            id
        )

        val temp = if (character.value != null) {
            character.value!!
        } else {
            characterRepository.getCharacterById(id)
        }
        clazz.value!!.level = toNumber(levels)
        temp.addClass(clazz.value!!, takeGold = takeGold.value)
        characterRepository.insertSpellSlots(temp.spellSlots, id)
        characterRepository.setHp(id, temp.maxHp.toString())

        //Persist feat choices and calculate ASIs.
        for ((i, item) in isFeat.withIndex()) {
            if (item) {
                characterRepository.addFeatsToCharacterClass(
                    characterId = id,
                    classId = clazz.value!!.id,
                    feats = getFeatsAt(
                        i,
                        toNumber(levels),
                        featDropDownStates,
                        featChoiceDropDownStates,
                        feats.value!!
                    )
                )
            } else {
                clazz.value!!.abilityImprovementsGranted.add(
                    (absDropDownStates[i].getSelected(shortStatNames) as List<String>).let {
                        val temp = mutableMapOf<String, Int>()
                        it.forEach { name ->
                            temp[name] = temp.getOrDefault(name, 0) + 1
                        }
                        temp
                    }
                )
            }
        }

        //Calculate equipment choices and proficiency choices,
        if (isBaseClass.value) {
            clazz.value!!.proficiencyChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>
            }

            if(!takeGold.value) {
                clazz.value!!.equipmentChoices.forEach {
                    it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<ItemInterface>>
                }

                //Store equipment choices
                characterRepository.insertCharacterClassEquipment(
                    clazz.value!!.equipmentChoices,
                    clazz.value!!.equipment,
                    id
                )
            } else {
                val gold = goldRolled.value.toIntOrNull()?.times(clazz.value!!.startingGoldMultiplier)
                characterRepository.setClassGold(gold ?: 0, id)
            }
        }

        //Insert a classChoiceEntity.
        characterRepository.insertClassChoiceEntity(
            ClassChoiceEntity(
                classId = clazz.value!!.id,
                characterId = id,
                level = toNumber(levels),
                isBaseClass = isBaseClass.value,
                totalNumOnGoldDie = goldRolled.value.toInt(),
                tookGold = takeGold.value,
                abilityImprovementsGranted = clazz.value!!.abilityImprovementsGranted,
                proficiencyChoicesByString = clazz.value!!.proficiencyChoices.toStringList(),
            )
        )

        clazz.value!!.pactMagic?.let {
            characterRepository.insertPactMagicStateEntity(
                characterId = id,
                classId = clazz.value!!.id,
                slotsCurrentAmount = it.pactSlots[toNumber(levels) - 1].currentAmount
            )
        }

        //Store all class spells.
        if (clazz.value!!.spellCasting != null || clazz.value!!.pactMagic != null) {
            val defaultPreparedness =
                if (clazz.value!!.spellCasting?.prepareFrom == null) false else null
            classSpells.forEach {
                characterRepository.insertCharacterClassSpellCrossRef(
                    classId = clazz.value!!.id,
                    spellId = it.id,
                    characterId = id,
                    prepared = defaultPreparedness
                )
            }
        }

        //If the level is high enough persist subclass choices
        if (toNumber(levels) >= clazz.value!!.level) {
            val subclass = (subclassDropdownState?.getSelected(
                subclasses.value ?: emptyList()
            ) as List<Subclass>).getOrNull(
                0
            )

            if (subclass != null) {
                characterRepository.insertCharacterSubclassCrossRef(
                    CharacterSubclassCrossRef(
                        subClassId = subclass.subclassId,
                        classId = clazz.value!!.id,
                        characterId = id
                    )
                )

                saveFeatures(subclass.features!!)

                if (subclass.spellCasting != null) {
                    val defaultPreparedness =
                        if (subclass.spellCasting?.prepareFrom == null) false else null
                    subclassSpells.forEach {
                        characterRepository.insertSubclassSpellCastingSpellCrossRef(
                            subclassId = subclass.subclassId,
                            spellId = it.id,
                            characterId = id,
                            isPrepared = defaultPreparedness
                        )
                    }
                }
            }
        }
    }


    fun getAsiNum(levels: Int): Int {
        return when (levels) {
            in 0..3 -> {
                0
            }
            in 4..7 -> {
                1
            }
            in 8..11 -> {
                2
            }
            in 12..15 -> {
                3
            }
            in 16..18 -> {
                4
            }
            in 19..20 -> {
                5
            }
            else -> 0
        }
    }

    private var subclassDropdownState: MultipleChoiceDropdownStateImpl? = null
    fun getSubclassDropdownState(): MultipleChoiceDropdownStateImpl {
        return if (subclassDropdownState == null) {
            subclassDropdownState = MultipleChoiceDropdownStateImpl()
            subclassDropdownState!!.maxSelections = 1
            subclassDropdownState!!.choiceName = "Subclass"
            val names = mutableListOf<String>()
            subclasses.value?.forEach {
                names.add(it.name)
            }
            subclassDropdownState!!.names = names
            subclassDropdownState!!.maxSameSelections = 1
            subclassDropdownState!!
        } else {
            val names = mutableListOf<String>()
            subclasses.value?.forEach {
                names.add(it.name)
            }
            subclassDropdownState!!.names = names
            subclassDropdownState!!
        }
    }

    fun canAffordMoreClassLevels(num: Int): Boolean {
        val className = clazz.value?.name ?: ""
        if ((character.value?.totalClassLevelsExcluding(className) ?: 0) + num <= 20) {
            return true
        }
        return false
    }

    val maxGoldRolled: Int
        get() {
            return clazz.value?.startingGoldD4s?.times(4) ?: 0
        }
    val minGoldRolled: Int
        get() {
            return clazz.value?.startingGoldD4s?.times(1) ?: 0
        }


    val hasBaseClass: Boolean
        get() {
            return if (character.value?.hasBaseClass == true) {
                //If the baseclass is the current class return false.
                character.value!!
                    .classes[clazz.value?.name]
                    ?.isBaseClass != true
            } else {
                false
            }
        }

    val learnableSpells = MutableLiveData<List<Spell>>()
    suspend fun calcLearnableSpells(level: Int, subclass: Subclass?) {
        learnableSpells.postValue(clazz.value?.id?.let {
            classRepository.getSpellsByClassId(it).run {
                subclass?.let {
                    //If the spells for the subclass arnt free add them to the selection.
                    if (!it.spellAreFree) {
                        val spells = mutableListOf<Spell>()
                        it.spells?.forEach { (_, spell) ->
                            spells.add(spell)
                        }
                        this.addAll(spells)
                    }
                }

                if (clazz.value?.spellCasting?.prepareFrom == "all") {
                    this.removeAll {
                        it.level != 0
                    }
                }
                try {
                    val maxLevel =
                        clazz.value?.spellCasting?.spellSlotsByLevel?.get(level - 1)?.size
                            ?: clazz.value?.pactMagic?.pactSlots?.get(level - 1)?.name?.toInt() ?: 0
                    this.removeAll {
                        it.level > maxLevel
                    }
                } catch (e: NumberFormatException) {
                    this.removeAll {
                        true
                    }
                }
                this
            }
        } ?: mutableListOf())
    }

    fun toggleClassSpell(it: Spell) {
        if (classSpells.contains(it)) {
            classSpells.remove(it)
        } else {
            classSpells.add(it)
        }
    }

    fun toggleSubclassSpell(it: Spell) {
        if (subclassSpells.contains(it)) {
            subclassSpells.remove(it)
        } else {
            subclassSpells.add(it)
        }
    }

    fun calculateAssumedFeatures(): List<Feature> {
        val result = mutableListOf<Feature>()
        clazz.value?.levelPath?.let { getFeatures(it, toNumber(levels)) }?.let {
            result.addAll(it)
        }

        if (toNumber(levels) >= clazz.value?.subclassLevel!!) {
            clazz.value?.subclass?.features?.let { getFeatures(it, toNumber(levels)) }?.let {
                result.addAll(it)
            }
        }

        return result
    }

    fun calculateAssumedSpells(): List<Spell> {
        val result = mutableListOf<Spell>()
        character.value?.let { characterRepository.getSpellsForCharacter(it) }?.let {
            it.forEach { (_, spells) ->
                spells.forEach { (_, spell) ->
                    result.add(spell)
                }
            }
        }
        isFeat.forEachIndexed { i, it ->
            if (it) {
                getFeatsAt(
                    i,
                    toNumber(levels),
                    featDropDownStates,
                    featChoiceDropDownStates,
                    feats.value!!
                ).forEach { feat ->
                    feat.features?.forEach {
                        result.addAll(it.getSpellsGiven())
                    }
                }
            }
        }
        result.addAll(subclassSpells)
        result.addAll(classSpells)
        return result
    }

    fun calculateAssumedStatBonuses(): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        val applyBonus = fun(name: String, amount: Int) {
            result[name.substring(0, 3)] =
                (result[name.substring(0, 3)] ?: 0) + amount
        }

        isFeat.forEachIndexed { i, it ->
            if (it) {
                getFeatsAt(
                    i,
                    toNumber(levels),
                    featDropDownStates,
                    featChoiceDropDownStates,
                    feats.value!!
                ).forEach { feat ->
                    feat.abilityBonuses?.forEach {
                        applyBonus(it.ability, it.bonus)
                    }
                    feat.abilityBonusChoice?.let { choice ->
                        choice.chosen.forEach {
                            applyBonus(it.ability, it.bonus)
                        }
                    }
                }
            } else {
                absDropDownStates[i].getSelected(shortStatNames).forEach { name ->
                        applyBonus(name as String, 1)
                    }
            }
        }
        return result
    }

    fun calculateAssumedProficiencies(): MutableList<Proficiency> {
        val profs: MutableList<Proficiency> = mutableListOf()
        clazz.value?.proficiencies?.let { profs.addAll(it) }
        clazz.value?.proficiencyChoices?.forEach {
            (dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>?)?.let { it1 ->
                profs.addAll(
                    it1
                )
            }
        }
        return profs
    }

    fun applyAlreadySelectedChoices() {
        character.value?.classes
            ?.getOrDefault(clazz.value?.name, null)?.let { clazzWithChoices ->
                //Apply level choice.
                levels.value = TextFieldValue(clazzWithChoices.level.toString())

                //Apply feature choices.
                clazzWithChoices.levelPath?.filter { it.grantedAtLevel <= clazzWithChoices.level }
                    ?.forEachIndexed { index, feature ->
                        feature.choices?.forEachIndexed { choiceIndex, _ ->
                            val featureToPass =
                                clazzWithChoices.levelPath?.filter { it.grantedAtLevel <= clazzWithChoices.level }
                                    ?.get(index)
                                    ?.copy()?.run {
                                        val featureWithOptions = clazz.value!!.levelPath!!.first { it.featureId == this.featureId }
                                        this.choices?.forEachIndexed { choiceIndex, it ->
                                            it.chosen = feature.choices!![choiceIndex].chosen
                                            it.options = featureWithOptions.choices?.get(choiceIndex)?.options
                                        }
                                        this
                                    }

                            featureToPass?.let {
                                featureDropdownStates.getDropDownState(
                                    choiceIndex = choiceIndex,
                                    feature = it,
                                    character = character.value,
                                    level = clazzWithChoices.level,
                                    assumedClass = null,
                                    assumedSpells = listOf(),
                                    assumedFeatures = listOf(),
                                    assumedProficiencies = listOf(),
                                    assumedStatBonuses = null
                                )
                            }
                        }
                    }


                //Apply base class choice
                isBaseClass.value = clazzWithChoices.isBaseClass
                if (isBaseClass.value) {
                    //Apply proficiency choices
                    clazzWithChoices.proficiencyChoices.forEach { choice ->
                        //Get or create the drop down state for the choice.
                        val names = mutableListOf<String>()
                        for (item in choice.from) {
                            names.add(item.name.toString())
                        }
                        val state = dropDownStates.getDropDownState(
                            key = choice.name,
                            maxSelections = choice.choose,
                            names = names,
                            choiceName = choice.name
                        )

                        //Apply data from the choice.
                        val selectedNames = mutableListOf<String>()
                        choice.chosen.forEach {
                            selectedNames.add(it.name.toString())
                        }
                        state.setSelected(selectedNames)
                    }

                    if (clazzWithChoices.tookGold == true) {
                        //Apply gold choices.
                        takeGold.value = true
                        goldRolled.value = clazzWithChoices.totalNumOnGoldDie.toString()
                    } else {
                        //Apply equipment choices.
                        clazzWithChoices.equipmentChoices.forEach { choice ->
                            //Get or create the drop down state for the choice.
                            val names = mutableListOf<String>()
                            for (item in choice.from) {
                                item.allNames.let { names.add(it) }
                            }

                            val state = dropDownStates.getDropDownState(
                                key = choice.name,
                                maxSelections = choice.choose,
                                names = names,
                                choiceName = choice.name
                            )
                            //Apply data from the choice.
                            val selectedIndexes = mutableListOf<Int>()
                            choice.chosen?.forEach {
                                selectedIndexes.add(choice.from.indexOf(it))
                            }
                            state.setSelected(selectedIndexes)
                        }
                    }
                }

                //Apply spell class choices.
                if (clazzWithChoices.spellCasting?.type != 0.0) {
                    clazzWithChoices.spellCasting?.known?.let { pairs -> classSpells.addAll(pairs.map { it.first }) }
                }
                clazzWithChoices.pactMagic?.let {
                    classSpells.addAll(it.known)
                }

                //Apply feat and asi choices.
                clazzWithChoices.featsGranted?.forEachIndexed { i, it ->
                    isFeat.add(i, true)
                    featNames.value?.let { featNames ->
                        featDropDownStates
                            .getDropDownState(
                                key = i,
                                maxSelections = 1,
                                names = featNames,
                                choiceName = "Feat"
                            )
                    }?.setSelected(mutableListOf(it.name))

                    it.features?.forEach { feature ->
                        feature.choices?.forEach { choice ->
                            val selected = choice.chosen.run {
                                val result = mutableListOf<String>()
                                this?.forEach {
                                    result.add(it.name)
                                }
                                result
                            }

                            featChoiceDropDownStates.getDropDownState(
                                key = "${feature.name}$i",
                                maxSelections = choice.choose.num(clazzWithChoices.level),
                                names = choice.options.let { featureList ->
                                    val result = mutableListOf<String>()
                                    featureList?.forEach {
                                        result.add(it.name)
                                    }
                                    result
                                },
                                choiceName = feature.name,
                                maxOfSameSelection = 1
                            ).setSelected(selected)
                        }
                    }
                }
                val offset = isFeat.size
                clazzWithChoices.abilityImprovementsGranted.forEachIndexed { i, it ->
                    isFeat.add(i + offset, false)
                    val state = absDropDownStates
                        .getDropDownState(
                            key = i,
                            maxSelections = 2,
                            names = CharacterRepository.statNames as MutableList<String>,
                            choiceName = "Ability Score Improvement",
                            maxOfSameSelection = 2
                        )
                    val selectedList = mutableListOf<Pair<String, Int>>()
                    it.forEach { entry ->
                        val key = CharacterRepository.statNames.first {
                            it.substring(0..2) == entry.key
                        }
                        selectedList.add(
                            Pair(key, entry.value)
                        )
                    }
                    state.setSelected(selectedList)
                }

                //Apply subclass choices.
                clazzWithChoices.subclass?.let { subclass ->
                    //Set the subclass
                    val state = getSubclassDropdownState()
                    state.setSelected(listOf(subclass.name))

                    //Apply subclass spell choices.
                    if (clazzWithChoices.spellCasting?.type != 0.0) {
                        clazzWithChoices.spellCasting?.known?.let { pairs -> subclassSpells.addAll(pairs.map { it.first }) }
                    }
                    clazzWithChoices.pactMagic?.let {
                        subclassSpells.addAll(it.known)
                    }
                }
            }
    }

    fun toNumber(textFieldValue: MutableState<TextFieldValue>): Int {
        return try {
            textFieldValue.value.text.toInt()
        } catch (e: NumberFormatException) {
            1
        }
    }
}