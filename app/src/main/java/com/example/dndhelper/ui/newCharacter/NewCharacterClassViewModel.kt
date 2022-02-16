package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class NewCharacterClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application){
    lateinit var classes : LiveData<List<Class>>
    var id = -1
    var isBaseClass =  mutableStateOf(true)
    var takeGold =  mutableStateOf(true)
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    var character : Character?  = null
    val spells = mutableStateListOf<Spell>()

    //ASIs
    private val shortAbilityNames  = mutableListOf(
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
    var feats : LiveData<List<Feat>>? = null
    val featNames: MediatorLiveData<MutableList<String>> = MediatorLiveData()
    val isFeat = mutableStateListOf<Boolean>()
    val featDropDownStates = mutableStateListOf<MultipleChoiceDropdownState>()
    val absDropDownStates = mutableStateListOf<MultipleChoiceDropdownState>()
    var classIndex = 0

    val proficiencies : List<Proficiency>
    get() {
        val profs : MutableList<Proficiency> = mutableListOf()
        classes.value?.get(classIndex)?.proficiencies?.let { profs.addAll(it) }
        classes.value?.get(classIndex)?.proficiencyChoices?.forEach {
            profs.addAll(dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>)
        }
        return profs
    }


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (E : Exception) {
            -1
        }
        viewModelScope.launch {
            classes = repository.getClasses()
            feats = repository.getFeats()
            character = repository.getCharacterById(id)
            isBaseClass.value = !(character?.hasBaseClass ?: false)
            featNames.addSource(feats!!) {
                val names = mutableListOf<String>()
                for(item in it) {
                    names.add(item.name)
                }
                featNames.value = names
            }
        }

    }




    suspend fun addClassLevels(newClass: Class, level: Int) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        newClass.level = level
        //TODO add a check for more than one base class
        if(isBaseClass.value) {
            newClass.isBaseClass = isBaseClass.value
            newClass.equipmentChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Item>
            }
            newClass.proficiencyChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>
            }
        }

        newClass.levelPath.filter { it.grantedAtLevel <= level }.forEach {
            if(it.choose.num(level) != 0 && it.options?.isNullOrEmpty() == false) {
                it.chosen = dropDownStates[it.name + it.grantedAtLevel]?.getSelected(it.getAvailableOptions(character, proficiencies)) as List<Feature>
            }
        }



        for((i, item) in isFeat.withIndex()) {
            if(item) {
                character!!.feats.addAll(featDropDownStates[i].getSelected(feats?.value!!) as List<Feat>)
            } else {
                character!!.addAbilityScoreIncreases(
                    (absDropDownStates[i].getSelected(shortAbilityNames) as List<Pair<String, Int>>)
                        .associateBy(
                            {it.first}, {it.second}
                        )
                )
            }
        }
        if(newClass.level > newClass.subclassLevel) {
            newClass.subclass =
                (subclassDropdownState?.getSelected(newClass.subClasses) as List<Subclass>).getOrNull(0)
        }

        if(newClass.spellCasting?.type != 0.0) {
            newClass.spellCasting?.known?.addAll(spells.toList())
        }

        character!!.addClass(newClass)
        character.let { repository.insertCharacter(it) }
    }

    fun getAsiNum(levels: Int): Int {
        return when(levels) {
            in 0..3 -> {
                0
            }
            in 4..7 -> {
                1
            }
            in 8..11 -> {
                2
            }
            in  12..15 -> {
                3
            }
            in  16..18 -> {
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
        return if(subclassDropdownState == null) {
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
        if((character?.totalClassLevels ?: 0) + num <= 20) {
            return true
        }
        return false
    }

    val hasBaseClass: Boolean
    get() {
        return character?.hasBaseClass ?: false
    }

    fun getSpells(classIndex: Int): MutableList<Spell> {
        return repository.getAllSpellsByClassIndex(classIndex).run {
            if(classes.value?.get(classIndex)?.spellCasting?.prepareFrom == "all") {
                this.removeAll {
                    it.level != 0
                }
            }
            this
        }
    }

    fun toggleSpell(it: Spell) {
        if(spells.contains(it)) {
            spells.remove(it)
        } else {
            spells.add(it)
        }
    }

    fun learnsSpells(classIndex: Int): Boolean {
        return (classes.value?.get(classIndex)?.spellCasting?.type ?: 0) != 0
    }

    fun canAffordSpellOfLevel(level: Int, classIndex: Int, classLevel: Int): Boolean {
        return if(level == 0) {
            classes.value!![classIndex].spellCasting!!.cantripsKnown!![classLevel - 1] > spells.count { it.level == level }
        } else {
            classes.value!![classIndex].spellCasting!!.spellsKnown!![classLevel - 1] > spells.count { it.level == level }
        }
    }

    fun totalSpells(classIndex: Int, level: MutableState<TextFieldValue>): Int {
        return try {
            classes.value?.getOrNull(classIndex)?.spellCasting?.spellsKnown?.getOrNull(
                level.value.text.toInt() - 1
            ) ?: 0
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun totalCantrips(classIndex: Int, level: MutableState<TextFieldValue>): Int {
        return try {
            classes.value?.getOrNull(classIndex)?.spellCasting?.cantripsKnown?.getOrNull(
                level.value.text.toInt() - 1
            ) ?: 0
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun getCastingMod(classIndex: Int): String {
        return abilityNames[shortAbilityNames.indexOf(classes.value?.getOrNull(classIndex)?.spellCasting?.castingAbility)]
    }


}

