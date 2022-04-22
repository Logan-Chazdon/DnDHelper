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
        return allSpellLevels.let { levels ->
            val result = mutableListOf<Pair<Int, String>>()
            levels.forEach { level ->
                val slot = character!!.value!!.getAllSpellSlots().findLast { it.name == level.second }
                if(slot?.currentAmount ?: 0 != 0 && spell.level <= level.first) {
                    result.add(level)
                }
            }
            result
        }
    }

    fun cast(spell: Spell, level: Int) {
        repository.insertCharacter(getCharacterMinusSlot(level))
    }

    fun refundSlot(slot: Int) {
        repository.insertCharacter(getCharacterPlusSlot(slot))
    }

    fun useSlot(slot: Int) {
        repository.insertCharacter(getCharacterMinusSlot(slot))
    }

    private fun getCharacterMinusSlot(slot: Int): Character {
        var result : Character? = null
        val newSlots = character!!.value!!.spellSlots
        if(newSlots.getOrNull(slot - 1)?.currentAmount ?: 0 != 0) {
            newSlots[slot - 1].currentAmount -= 1
            result =  character!!.value!!.copy(spellSlots = newSlots, id = character!!.value!!.id)
        } else {
            for ((_, clazz) in character!!.value!!.classes) {
                if(clazz.pactMagic?.pactSlots?.get(clazz.level - 1)?.currentAmount != 0) {
                    clazz.pactMagic?.pactSlots!![clazz.level - 1].currentAmount -= 1
                    result = character!!.value!!.copy(id = character!!.value!!.id)
                    break
                }
            }
        }
        return result!!
    }

    private fun getCharacterPlusSlot(slot: Int): Character {
        var result : Character? = null
        val newSlots = character!!.value!!.spellSlots
        if(
            newSlots.getOrNull(slot - 1)?.currentAmount ?: 0
            != (newSlots.getOrNull(slot - 1)?.maxAmountType ?: "0").toInt()
        ) {
            newSlots[slot - 1].currentAmount += 1
            result =  character!!.value!!.copy(spellSlots = newSlots, id = character!!.value!!.id)
        } else {
            for ((_, clazz) in character!!.value!!.classes) {
                if(
                    clazz.pactMagic?.pactSlots?.get(clazz.level - 1)?.currentAmount !=
                    clazz.pactMagic?.pactSlots?.get(clazz.level - 1)?.maxAmountType?.toInt()
                ) {
                    clazz.pactMagic?.pactSlots!![clazz.level - 1].currentAmount += 1
                    result = character!!.value!!.copy(id = character!!.value!!.id)
                    break
                }
            }
        }
        return result!!
    }

    fun getSpellSlotsAndCantrips(): MutableList<Resource> {
        val spellSlotsOffsetForCantrips = mutableListOf(
            Resource("Cantrip", 0, "0", "0")
        )
        character?.value?.getAllSpellSlots()?.let { spellSlotsOffsetForCantrips.addAll(it) }
        return spellSlotsOffsetForCantrips
    }

    //Returns a list of booleans to spells
    //If the boolean is null the spell does not require preparation.
    //Else the boolean represents whether or not the spell is prepared.
    fun getAllSpells(): Map<Int, List<Pair<Boolean?, Spell>>> {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        character?.value?.classes?.forEach {
            addSpellsFromSpellCasting(it.value.spellCasting, listOf(it.value.name), spells)
            it.value.subclass?.spellCasting?.let { spellCasting ->
                addSpellsFromSpellCasting(spellCasting, spellCasting.learnFrom, spells)
            }
        }

        character?.value?.additionalSpells?.forEach {
            spells[it.key]?.addAll(it.value)
        }

        character?.value?.classes?.forEach { clazz ->
            clazz.value.pactMagic?.known?.let { known ->
                known.forEach {
                    if(spells.getOrDefault(it.level, null) == null) {
                        spells[it.level] = mutableListOf()
                    }
                    spells[it.level]?.add(Pair(null, it))
                }
            }
        }
        return spells
    }

    private fun addSpellsFromSpellCasting(
        spellCasting: SpellCasting?,
        lists: List<String>?,
        spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>>
    ) {
        //Preparation casters
        //TODO the time complexity here is really bad. Refactor this
        when(spellCasting?.prepareFrom) {
            null -> {
                //Non preparation casters
                spellCasting?.known?.forEach { spell ->
                    if(spells.getOrDefault(spell.level, null) == null) {
                        spells[spell.level] = mutableListOf()
                    }
                    spells[spell.level]?.add(Pair(first = null, second = spell))
                }
            }
            "all" -> {
                //Spell casters that prepare from all of their respective class spells
                spellCasting.known.forEach { spell ->
                    if(spell.level == 0) {
                        if (spells.getOrDefault(spell.level, null) == null) {
                            spells[spell.level] = mutableListOf()
                        }
                        spells[spell.level]?.add(Pair(first = null, second = spell))
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
                    repository.getAllSpellsByClassIndex(
                        repository.getClassIndex(it)
                    ).forEach { spell ->
                        spellCasting.prepared.contains(spell).let { prepared ->
                            if (spell.level != 0) {
                                if (spells.getOrDefault(spell.level, null) == null) {
                                    spells[spell.level] = mutableListOf()
                                }
                                spells[spell.level]?.add(Pair(prepared, spell))
                            }
                        }
                    }
                }
            }
            "known" -> {
                //Spell casters that prepare from known spells
                spellCasting.known.forEach { spell ->
                    spellCasting.prepared.contains(spell).let { prepared ->
                        if(spells.getOrDefault(spell.level, null) == null) {
                            spells[spell.level] = mutableListOf()
                        }
                        spells[spell.level]?.add(Pair(prepared, spell))
                    }
                }
            }
        }

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