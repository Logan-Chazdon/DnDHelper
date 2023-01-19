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
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.model.repositories.Repository.Companion.shortStatNames
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.utils.getFeatsAt
import gmail.loganchazdon.dndhelper.ui.utils.toStringList
import javax.inject.Inject


@HiltViewModel
class NewCharacterConfirmClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val clazz = repository.getClass(savedStateHandle.get<String>("classId")!!.toInt())
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
    val feats: LiveData<List<Feat>> = repository.getFeats()
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()
    val subclasses =
        repository.getSubclassesByClassId(savedStateHandle.get<String>("classId")!!.toInt())

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
            repository.getLiveCharacterById(
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
                    repository.insertFeatureChoiceChoiceEntity(
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
            id = repository.createDefaultCharacter() ?: -1

        repository.insertCharacterClassCrossRef(
            characterId = id,
            classId = clazz.value!!.id
        )

        saveFeatures(clazz.value!!.levelPath!!)

        //Persist all feature choices.


        //Persist feat choices and calculate ASIs.
        for ((i, item) in isFeat.withIndex()) {
            if (item) {
                repository.addFeatsToCharacterClass(
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
                    (absDropDownStates[i].getSelected(shortStatNames) as List<Pair<String, Int>>)
                        .associateBy(
                            { it.first }, { it.second }
                        )
                )
            }
        }

        //Calculate equipment choices and proficiency choices,
        if (isBaseClass.value) {
            clazz.value!!.equipmentChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
            }
            clazz.value!!.proficiencyChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>
            }

            //Store equipment choices
            repository.insertCharacterClassEquipment(
                clazz.value!!.equipmentChoices,
                clazz.value!!.equipment,
                id
            )
        }

        //Insert a classChoiceEntity.
        repository.insertClassChoiceEntity(
            ClassChoiceEntity(
                classId = clazz.value!!.id,
                characterId = id,
                level = toNumber(levels),
                isBaseClass = isBaseClass.value,
                totalNumOnGoldDie = goldRolled.value.toInt(),
                tookGold = takeGold.value,
                abilityImprovementsGranted = clazz.value!!.abilityImprovementsGranted,
                proficiencyChoices = clazz.value!!.proficiencyChoices.toStringList(),
            )
        )

        //Store all class spells.
        if (clazz.value!!.spellCasting != null || clazz.value!!.pactMagic != null) {
            val defaultPreparedness =
                if (clazz.value!!.spellCasting?.prepareFrom == null) false else null
            classSpells.forEach {
                repository.insertCharacterClassSpellCrossRef(
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

            repository.insertCharacterSubclassCrossRef(
                CharacterSubclassCrossRef(
                    subClassId = subclass!!.subclassId,
                    classId = clazz.value!!.id,
                    characterId = id
                )
            )

            saveFeatures(subclass.features!!)

            if (subclass.spellCasting != null) {
                val defaultPreparedness =
                    if (subclass.spellCasting?.prepareFrom == null) false else null
                subclassSpells.forEach {
                    repository.insertSubclassSpellCastingSpellCrossRef(
                        subclassId = subclass.subclassId,
                        spellId = it.id,
                        characterId = id,
                        isPrepared = defaultPreparedness
                    )
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
            repository.getSpellsByClassId(it).run {
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
        character.value?.let { repository.getSpellsForCharacter(it) }?.let {
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
                (absDropDownStates[i].getSelected(shortStatNames) as List<Pair<String, Int>>)
                    .associateBy(
                        { it.first }, { it.second }
                    ).forEach { (name, bonus) ->
                        applyBonus(name, bonus)
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

    fun applyAlreadySelectedChoices() {/*
        val className = classes.value?.get(classIndex)?.name
        character?.value?.classes
            ?.getOrDefault(className, null)?.let { clazz ->
                //Apply level choice.
                levels.value = TextFieldValue(clazz.level.toString())

                //Apply feature choices.
                clazz.levelPath.filter { it.grantedAtLevel <= clazz.level }
                    .forEachIndexed { index, feature ->
                        feature.choices?.forEachIndexed { choiceIndex, _ ->
                            val featureToPass = classes.value?.get(classIndex)
                                ?.levelPath?.filter { it.grantedAtLevel <= clazz.level }?.get(index)
                                ?.copy()?.run {
                                    this.choices?.forEachIndexed { choiceIndex, it ->
                                        it.chosen = feature.choices[choiceIndex].chosen
                                    }
                                    this
                                }

                            featureToPass?.let {
                                featureDropdownStates.getDropDownState(
                                    choiceIndex = choiceIndex,
                                    feature = it,
                                    character = character.value,
                                    level = clazz.level,
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
                isBaseClass.value = clazz.isBaseClass
                if (isBaseClass.value) {
                    //Apply proficiency choices
                    clazz.proficiencyChoices.forEach { choice ->
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
                        choice.chosen?.forEach {
                            selectedNames.add(it.name.toString())
                        }
                        state.setSelected(selectedNames)
                    }

                    if (clazz.tookGold == true) {
                        //Apply gold choices.
                        takeGold.value = true
                        goldRolled.value = clazz.totalNumOnGoldDie.toString()
                    } else {
                        //Apply equipment choices.
                        clazz.equipmentChoices.forEach { choice ->
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
                if (clazz.spellCasting?.type != 0.0) {
                    clazz.spellCasting?.known?.let { classSpells.addAll(it) }
                }
                clazz.pactMagic?.let {
                    classSpells.addAll(it.known)
                }

                //Apply feat and asi choices.
                clazz.featsGranted.forEachIndexed { i, it ->
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
                                maxSelections = choice.choose.num(clazz.level),
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
                clazz.abilityImprovementsGranted.forEachIndexed { i, it ->
                    isFeat.add(i + offset, false)
                    val state = absDropDownStates
                        .getDropDownState(
                            key = i,
                            maxSelections = 2,
                            names = abilityNames,
                            choiceName = "Ability Score Improvement",
                            maxOfSameSelection = 2
                        )
                    val selectedList = mutableListOf<Pair<String, Int>>()
                    it.forEach { entry ->
                        val key = abilityNames.first {
                            it.substring(0..2) == entry.key
                        }
                        selectedList.add(
                            Pair(key, entry.value)
                        )
                    }
                    state.setSelected(selectedList)
                }

                //Apply subclass choices.
                clazz.subclass?.let { subclass ->
                    //Set the subclass
                    val state = getSubclassDropdownState(clazz)
                    state.setSelected(listOf(subclass.name))

                    //Apply subclass spell choices.
                    if (clazz.spellCasting?.type != 0.0) {
                        clazz.spellCasting?.known?.let { subclassSpells.addAll(it) }
                    }
                    clazz.pactMagic?.let {
                        subclassSpells.addAll(it.known)
                    }
                }
            }
            */
    }

    fun toNumber(textFieldValue: MutableState<TextFieldValue>): Int {
        return try {
            textFieldValue.value.text.toInt()
        } catch (e: NumberFormatException) {
            1
        }
    }
}