package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.Repository.Companion.allSpellLevels
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Resource
import com.example.dndhelper.repository.dataClasses.Spell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class CombatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {

    fun addTemp(temp: String) {
        val tempChar = character!!.value!!.copy(tempHp = temp.toInt())
        tempChar.id = character!!.value!!.id
        repository.insertCharacter(tempChar)
    }

    fun heal(temp: String) {
        var newHp = character!!.value!!.currentHp + temp.toInt()
        if(newHp > character!!.value!!.maxHp) {
            newHp = character!!.value!!.maxHp
        }

        val tempChar = character!!.value!!.copy(currentHp = newHp)
        tempChar.id = character!!.value!!.id
        repository.insertCharacter(tempChar)
    }

    fun setHp(it: String) {
        val tempChar = character!!.value!!.copy(currentHp = it.toInt())
        tempChar.id = character!!.value!!.id
        repository.insertCharacter(tempChar)
    }

    fun damage(temp: String) {
        var currentHp = character!!.value!!.currentHp
        var tempHp = 0
        if(character!!.value!!.tempHp < temp.toInt()) {
            val amountRemoved = character!!.value!!.tempHp
            currentHp -= temp.toInt() - amountRemoved

        } else {
            tempHp = character!!.value!!.tempHp - temp.toInt()
        }

        val tempChar = character!!.value!!.copy(currentHp = currentHp, tempHp = tempHp)
        tempChar.id = character!!.value!!.id

        repository.insertCharacter(tempChar)
    }

    fun updateDeathSaveSuccesses(it: Boolean) {
        val tempChar : Character = if(it){
            character!!.value!!.copy(positiveDeathSaves = character!!.value!!.positiveDeathSaves + 1)
        } else {
            character!!.value!!.copy(positiveDeathSaves = character!!.value!!.positiveDeathSaves - 1)
        }
        repository.insertCharacter(tempChar)
    }

    fun updateDeathSaveFailures(it: Boolean) {
        val tempChar : Character = if(it){
            character!!.value!!.copy(negativeDeathSaves = character!!.value!!.negativeDeathSaves + 1)
        } else {
            character!!.value!!.copy(negativeDeathSaves = character!!.value!!.negativeDeathSaves - 1)
        }
        repository.insertCharacter(tempChar)
    }

    fun getCastingOptions(spell: Spell): List<Pair<Int, String>> {
        return allSpellLevels.subList(
            spell.level - 1,
            character!!.value!!.spellSlots.lastIndex + 1
        ).let { levels ->
            val result = mutableListOf<Pair<Int, String>>()
            levels.forEach {
                if(character!!.value!!.spellSlots[it.first - 1].currentAmount != 0) {
                    result.add(it)
                }
            }
            result
        }
    }

    fun cast(spell: Spell, level: Int) {
        val newSlots = character!!.value!!.spellSlots
        newSlots[level-1].currentAmount -= 1
        val tempChar : Character =
            character!!.value!!.
            copy(spellSlots = newSlots)
        repository.insertCharacter(tempChar)
    }

    fun refundSlot(slot: Int) {
        val newSlots = character!!.value!!.spellSlots
        newSlots[slot-1].currentAmount += 1
        val tempChar : Character =
            character!!.value!!.
            copy(spellSlots = newSlots)
        repository.insertCharacter(tempChar)
    }

    fun useSlot(slot: Int) {
        val newSlots = character!!.value!!.spellSlots
        newSlots[slot-1].currentAmount -= 1
        val tempChar : Character =
            character!!.value!!.
            copy(spellSlots = newSlots)
        repository.insertCharacter(tempChar)
    }

    fun getSpellSlotsAndCantrips(): MutableList<Resource> {
        val spellSlotsOffsetForCantrips = mutableListOf(
            Resource("Cantrip", 0, "0", "0")
        )
        character?.value?.spellSlots?.let { spellSlotsOffsetForCantrips.addAll(it) }
        return spellSlotsOffsetForCantrips
    }

    //Returns a list of booleans to spells
    //If the boolean is null the spell does not require preparation.
    //Else the boolean represents whether or not the spell is prepared.
    //TODO this entire function is garbage dear lord please fix this.
    fun getAllSpells(): Map<Int, List<Pair<Boolean?, Spell>>> {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        character?.value?.classes?.forEach {
                //Preparation casters
                //TODO the time complexity here is really bad. Refactor this
                when(it.value.spellCasting?.prepareFrom) {
                    null -> {
                        //Non preparation casters
                        it.value.spellCasting?.known?.forEach { spell ->
                            if(spells.getOrDefault(spell.level, null) == null) {
                                spells[spell.level] = mutableListOf()
                            }
                            spells[spell.level]?.add(Pair(first = null, second = spell))
                        }
                    }
                    "all" -> {
                        //Spell casters that prepare from all of their respective class spells
                        it.value.spellCasting?.known?.forEach { spell ->
                            if(spell.level == 0) {
                                if (spells.getOrDefault(spell.level, null) == null) {
                                    spells[spell.level] = mutableListOf()
                                }
                                spells[spell.level]?.add(Pair(first = null, second = spell))
                            }
                        }

                        repository.getAllSpellsByClassIndex(
                            repository.getClassIndex(it.value.name)
                        ).forEach { spell ->
                            it.value.spellCasting?.prepared?.contains(spell).let { prepared ->
                                if(spell.level != 0) {
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
                        it.value.spellCasting?.known?.forEach { spell ->
                            it.value.spellCasting?.prepared?.contains(spell).let { prepared ->
                                if(spells.getOrDefault(spell.level, null) == null) {
                                    spells[spell.level] = mutableListOf()
                                }
                                spells[spell.level]?.add(Pair(prepared, spell))
                            }
                        }
                    }
                }
        }
        return spells
    }

    fun togglePreparation(spell: Spell) {
        for (item in character?.value!!.classes.values) {
            if (
            //This class prepares spells.
                item.spellCasting?.prepareFrom != null &&
                //This class has access to the spell.
                spell.classes.contains(item.name.lowercase())
            ) {
                //If we have the spell prepared unprepared it.
                if (item.spellCasting.prepared.contains(spell)) {
                    item.spellCasting.prepared.remove(spell)
                    break
                }

                //If this class can prepare more spells.
                if (
                    item.spellCasting.getMaxPrepared(
                        item.level,
                        character!!.value?.getStatMod(item.spellCasting.castingAbility) ?: 1
                    ) > item.spellCasting.prepared.size
                ) {
                    item.spellCasting.prepared.add(spell)
                    break
                }
            }
        }
        character?.value?.let { repository.insertCharacter(it) }
    }

    var character : LiveData<Character>? = null


    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()
        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
        }

    }
}