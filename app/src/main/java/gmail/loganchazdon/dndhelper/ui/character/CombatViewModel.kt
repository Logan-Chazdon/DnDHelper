package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Resource
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository
import gmail.loganchazdon.dndhelper.model.repositories.SpellRepository.Companion.allSpellLevels
import javax.inject.Inject

@HiltViewModel
public class CombatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: CharacterRepository, application: Application
): AndroidViewModel(application) {
    fun setTemp(temp: String) {
        repository.setTemp(character.value?.id, temp)
    }

    fun heal(hp: String) {
        repository.heal(character.value?.id, hp)
    }

    fun setHp(hp: String) {
        repository.setHp(character.value?.id, hp)
    }

    fun damage(damage: String) {
        repository.damage(character.value?.id, damage)
    }

    fun updateDeathSaveSuccesses(it: Boolean) {
        repository.updateDeathSaveSuccesses(character.value?.id, it)
    }

    fun updateDeathSaveFailures(it: Boolean) {
        repository.updateDeathSaveFailures(character.value?.id, it)
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
       /* var result : Character? = null
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
        return result!!*/
        return Character()
    }

    private fun getCharacterPlusSlot(slot: Int): Character {
        /*var result : Character? = null
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
        return result!!*/
        return Character()
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
        character.value?.let {
            return repository.getSpellsForCharacter(it)
        }
        return mapOf()
    }



    fun togglePreparation(spell: Spell) {
        /*for (item in character?.value!!.classes.values) {
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
        character.value?.let { repository.insertCharacter(it) }
        */
        //TODO refactor
    }

    val character : MediatorLiveData<Character> = MediatorLiveData()


    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )
    }
}