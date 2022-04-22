package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.*
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import com.example.dndhelper.ui.utils.allNames
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
public class NewCharacterClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var classes: LiveData<List<Class>> = repository.getClasses()
    var id = -1
    var isBaseClass = mutableStateOf(true)
    var takeGold = mutableStateOf(false)
    var goldRolled = mutableStateOf("6")
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    val character: LiveData<Character>?
    val classSpells = mutableStateListOf<Spell>()
    val subclassSpells = mutableStateListOf<Spell>()
    val levels = mutableStateOf(TextFieldValue("1"))

    //ASIs
    private val shortAbilityNames = mutableListOf(
        "Str",
        "Dex",
        "Con",
        "Int",
        "Wis",
        "Cha"
    )

    val abilityNames = mutableListOf(
        "Strength",
        "Dexterity",
        "Constitution",
        "Intelligence",
        "Wisdom",
        "Charisma"
    )
    val feats: LiveData<List<Feat>> = repository.getFeats()
    val featNames: MediatorLiveData<MutableList<String>> = MediatorLiveData()
    val isFeat = mutableStateListOf<Boolean>()
    val featDropDownStates = mutableStateListOf<MultipleChoiceDropdownState>()
    val featChoiceDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    val absDropDownStates = mutableStateListOf<MultipleChoiceDropdownState>()
    var classIndex = 0

    val proficiencies: List<Proficiency>
        get() {
            val profs: MutableList<Proficiency> = mutableListOf()
            classes.value?.get(classIndex)?.proficiencies?.let { profs.addAll(it) }
            classes.value?.get(classIndex)?.proficiencyChoices?.forEach {
                (dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>?)?.let { it1 ->
                    profs.addAll(
                        it1
                    )
                }
            }
            return profs
        }


    fun toNumber(textFieldValue: MutableState<TextFieldValue>): Int {
        return try {
            textFieldValue.value.text.toInt()
        } catch (e: NumberFormatException) {
            1
        }
    }


    init {
        featNames.addSource(feats) {
            val names = mutableListOf<String>()
            for (item in it) {
                names.add(item.name)
            }
            featNames.value = names
        }

        classIndex = try {
            savedStateHandle.get<String>("classIndex")!!.toInt()
        } catch (e: Exception) {
            0
        }

        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        character = try {
            repository.getLiveCharacterById(id)!!
        } catch (e: NullPointerException) {
            null
        }

        isBaseClass.value = !(character?.value?.hasBaseClass ?: false)
    }

    private fun getFeatsAt(i: Int, level: Int) : List<Feat> {
        return (featDropDownStates[i].getSelected(feats.value!!) as List<Feat>).run {
                this.forEach { feat ->
                    feat.features?.forEach { feature ->
                        if(feature.choose.num(level) != 0) {
                            feature.chosen = feature.options?.let {
                                (
                                        featChoiceDropDownStates.getDropDownState(
                                            key = "${feature.name}$i",
                                            maxSelections = feature.choose.num(level),
                                            names = feature.options.let { featureList ->
                                                val result = mutableListOf<String>()
                                                featureList.forEach {
                                                    result.add(it.name)
                                                }
                                                result
                                            },
                                            choiceName = feature.name,
                                            maxOfSameSelection = 1
                                        )
                                        ).getSelected(it)
                            } as List<Feature>
                        }
                    }
                }
                this
            }
    }


    suspend fun addClassLevels(newClass: Class, level: Int) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        newClass.level = level
        newClass.tookGold = takeGold.value
        newClass.totalNumOnGoldDie = goldRolled.value.toInt()

        if (isBaseClass.value) {
            newClass.isBaseClass = isBaseClass.value
            newClass.equipmentChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
            }
            newClass.proficiencyChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>
            }
        }

        newClass.levelPath.filter { it.grantedAtLevel <= level }.forEach {
            if (it.choose.num(level) != 0 && it.options?.isNullOrEmpty() == false) {
                it.chosen = dropDownStates[it.name + it.grantedAtLevel]?.getSelected(
                    it.getAvailableOptions(
                        character = character,
                        assumedProficiencies = proficiencies,
                        level = level,
                        assumedStatBonuses = calculateAssumedStatBonuses(),
                        assumedSpells = calculateAssumedSpells(),
                        assumedClass = classes.value?.get(classIndex)
                    )
                ) as List<Feature>
            }
        }

        for ((i, item) in isFeat.withIndex()) {
            if (item) {
                newClass.featsGranted.addAll(
                    getFeatsAt(i, level)
                )
            } else {
                newClass.abilityImprovementsGranted.add(
                    (absDropDownStates[i].getSelected(shortAbilityNames) as List<Pair<String, Int>>)
                        .associateBy(
                            { it.first }, { it.second }
                        )
                )
            }
        }
        if (newClass.level >= newClass.subclassLevel) {
            newClass.subclass =
                (subclassDropdownState?.getSelected(newClass.subClasses) as List<Subclass>).getOrNull(
                    0
                ).run {
                    if (this?.spellCasting != null) {
                        this.spellCasting?.known?.addAll(subclassSpells)
                    }
                    this
                }

            newClass.subclass?.features?.filter { it.grantedAtLevel <= level }?.forEach {
                if (it.choose.num(level) != 0 && it.options?.isNullOrEmpty() == false) {
                    it.chosen = dropDownStates[it.name + it.grantedAtLevel]
                        ?.getSelected(
                            it.getAvailableOptions(
                                character = character,
                                assumedProficiencies = proficiencies,
                                level = level,
                                assumedStatBonuses = calculateAssumedStatBonuses(),
                                assumedSpells = calculateAssumedSpells(),
                                assumedClass = classes.value?.get(classIndex)
                            )
                        )
                            as List<Feature>
                }
            }
        }

        if (newClass.spellCasting?.type != 0.0) {
            newClass.spellCasting?.known?.addAll(classSpells.toList())
        }


        newClass.pactMagic?.known?.addAll(classSpells.toList())
        if (newClass.subclass?.spellAreFree == true) {
            val spellsGrantedBySubclass = newClass.subclass?.spells?.let {
                val result = mutableListOf<Spell>()
                it.forEach { (level, spell) ->
                    if (level >= newClass.level) {
                        result.add(spell)
                    }
                }
                result
            }

            spellsGrantedBySubclass?.let {
                newClass.pactMagic?.known?.addAll(it)
                newClass.spellCasting?.known?.addAll(it)
            }
        }

        character!!.addClass(newClass, takeGold.value)
        character.let { repository.insertCharacter(it) }
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

    private var subclassDropdownState: MultipleChoiceDropdownState? = null
    fun getSubclassDropdownState(it: Class): MultipleChoiceDropdownState {
        return if (subclassDropdownState == null) {
            subclassDropdownState = MultipleChoiceDropdownState()
            subclassDropdownState!!.maxSelections = 1
            subclassDropdownState!!.choiceName = "Subclass"
            val names = mutableListOf<String>()
            it.subClasses.forEach {
                names.add(it.name)
            }
            subclassDropdownState!!.names = names
            subclassDropdownState!!.maxSameSelections = 1
            subclassDropdownState!!
        } else {
            subclassDropdownState!!
        }
    }

    fun canAffordMoreClassLevels(num: Int): Boolean {
        if ((character?.value?.totalClassLevels ?: 0) + num <= 20) {
            return true
        }
        return false
    }

    val hasBaseClass: Boolean
        get() {
            return if (character?.value?.hasBaseClass == true) {
                //If the baseclass is the current class return false.
                character.value!!
                    .classes[classes.value?.get(classIndex)?.name]
                    ?.isBaseClass != true
            } else {
                false
            }
        }

    fun getLearnableSpells(level: Int, subclass: Subclass?): MutableList<Spell> {
        return repository.getAllSpellsByClassIndex(classIndex).run {
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

            if (classes.value?.get(classIndex)?.spellCasting?.prepareFrom == "all") {
                this.removeAll {
                    it.level != 0
                }
            }
            try {
                val maxLevel =
                    classes.value?.get(classIndex)?.spellCasting?.spellSlotsByLevel?.get(level - 1)?.size
                        ?: classes.value?.get(classIndex)?.pactMagic?.pactSlots?.get(level - 1)!!.name.toInt()
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
    }

    fun getLearnableSpells(subclass: Subclass, level: Int): List<Spell> {
        val result = mutableListOf<Spell>()
        val getClassIndexByName = fun(name: String): Int {
            classes.value?.forEachIndexed { index, clazz ->
                if (name.lowercase() == clazz.name.lowercase()) {
                    return index
                }
            }
            return 0 //TODO make custom exception
        }

        val maxLevel = subclass.spellCasting?.spellSlotsByLevel?.get(level - 1)!!.size

        subclass.spellCasting?.learnFrom?.forEach {
            result.addAll(
                repository.getAllSpellsByClassIndex(
                    getClassIndexByName(it)
                )
            )
        }

        if (subclass.spellCasting?.prepareFrom == "all") {
            result.removeAll {
                it.level != 0
            }
        }

        result.removeAll {
            it.level > maxLevel
        }

        return result
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

    fun removeClass(name: String) {
        val newClasses = character?.value?.classes?.run {
            this.remove(name)
            this
        }
        val newChar = character?.value?.copy(classes = newClasses!!)
        newChar?.id = character?.value?.id!!
        repository.insertCharacter(newChar!!)
    }

    fun calculateAssumedSpells() : List<Spell> {
        val result = mutableListOf<Spell>()
        character?.value?.let { repository.getSpellsForCharacter(it) }?.let {
            it.forEach { (_, spells) ->
                spells.forEach { (_, spell) ->
                    result.add(spell)
                }
            }
        }
        isFeat.forEachIndexed { i, it ->
            if(it) {
                getFeatsAt(i, toNumber(levels)).forEach { feat ->
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
                getFeatsAt(i, toNumber(levels)).forEach { feat ->
                    feat.abilityBonuses?.forEach {
                        applyBonus(it.ability, it.bonus)
                    }
                    feat.abilityBonusChoice?.let { choice ->
                        choice.chosen?.forEach {
                            applyBonus(it.ability, it.bonus)
                        }
                    }
                }
            } else {
                (absDropDownStates[i].getSelected(shortAbilityNames) as List<Pair<String, Int>>)
                    .associateBy(
                        { it.first }, { it.second }
                    ).forEach { (name, bonus) ->
                        applyBonus(name, bonus)
                    }
            }
        }
        return result
    }


    fun applyAlreadySelectedChoices() {
        val className = classes.value?.get(classIndex)?.name
        character?.value?.classes
            ?.getOrDefault(className, null)?.let { clazz ->
                val applyFeature = fun(feature: Feature) {
                    val maxSelections = feature.choose.num(toNumber(levels))
                    if (maxSelections != 0) {
                        val state = dropDownStates.getDropDownState(
                            key = feature.name + feature.grantedAtLevel,
                            choiceName = feature.name,
                            maxSelections = maxSelections,
                            names = feature.options.let { list ->
                                val result = mutableListOf<String>()
                                list?.forEach {
                                    result.add(it.name)
                                }
                                result
                            }
                        )
                        state.setSelected(
                            feature.chosen.let { list ->
                                val result = mutableListOf<String>()
                                list?.forEach {
                                    result.add(it.name)
                                }
                                result
                            }
                        )
                    }
                }

                //Apply level choice.
                levels.value = TextFieldValue(clazz.level.toString())

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

                //Apply class feature choices.
                clazz.levelPath.forEach {
                    applyFeature(it)
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

                    //Set the subclass choices.
                    subclass.features.forEach {
                        applyFeature(it)
                    }
                }
            }
    }
}



