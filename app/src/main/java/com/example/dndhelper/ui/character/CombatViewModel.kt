package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.Repository.Companion.allSpellLevels
import com.example.dndhelper.repository.dataClasses.Character
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

    var character : LiveData<Character>? = null


    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()
        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
        }

    }
}