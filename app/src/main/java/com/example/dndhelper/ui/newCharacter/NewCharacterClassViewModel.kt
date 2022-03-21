package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
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
    var takeGold =  mutableStateOf(false)
    var goldRolled = mutableStateOf("6")
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    var character : Character?  = null
    val classSpells = mutableStateListOf<Spell>()
    val subclassSpells = mutableStateListOf<Spell>()

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
            (dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>?)?.let { it1 ->
                profs.addAll(
                    it1
                )
            }
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
                it.chosen = dropDownStates[it.name + it.grantedAtLevel]?.getSelected(it.getAvailableOptions(character, proficiencies, level)) as List<Feature>
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
        if(newClass.level >= newClass.subclassLevel) {
            newClass.subclass =
                (subclassDropdownState?.getSelected(newClass.subClasses) as List<Subclass>).getOrNull(0).run {
                    if(this?.spellCasting != null) {
                        this.spellCasting?.known?.addAll(subclassSpells)
                    }
                    this
                }
        }

        if(newClass.spellCasting?.type != 0.0) {
            newClass.spellCasting?.known?.addAll(classSpells.toList())
        }



        newClass.pactMagic?.known?.addAll(classSpells.toList())
        //TODO return to the level system.
        if(newClass.subclass?.spellAreFree == true) {
            val spellsGrantedBySubclass = newClass.subclass?.spells?.let {
                val result = mutableListOf<Spell>()
                it.forEach { (level, spell) ->
                    if(level >= newClass.level) {
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

        character!!.addClass(newClass, takeGold.value, goldRolled.value.toInt() * newClass.startingGoldMultiplier)
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

    fun getLearnableSpells(level: Int, subclass: Subclass?): MutableList<Spell> {
        return repository.getAllSpellsByClassIndex(classIndex).run {
            subclass?.let {
                //If the spells for the subclass arnt free add them to the selection.
                if(!it.spellAreFree) {
                    val spells = mutableListOf<Spell>()
                    it.spells?.forEach { (_, spell) ->
                        spells.add(spell)
                    }
                    this.addAll(spells)
                }
            }

            if(classes.value?.get(classIndex)?.spellCasting?.prepareFrom == "all") {
                this.removeAll {
                    it.level != 0
                }
            }
            try {
                val maxLevel = classes.value?.get(classIndex)?.spellCasting?.spellSlotsByLevel?.get(level - 1)?.size
                    ?: classes.value?.get(classIndex)?.pactMagic?.pactSlots?.get(level - 1)!!.name.toInt()
                this.removeAll {
                    it.level > maxLevel
                }
            } catch(e : NumberFormatException) {
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

        if(subclass.spellCasting?.prepareFrom == "all") {
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
        if(classSpells.contains(it)) {
            classSpells.remove(it)
        } else {
            classSpells.add(it)
        }
    }

    fun toggleSubclassSpell(it: Spell) {
        if(subclassSpells.contains(it)) {
            subclassSpells.remove(it)
        } else {
            subclassSpells.add(it)
        }
    }
}

