package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
    private val repository: CharacterRepository, application: Application
) : AndroidViewModel(application) {
    private val characterKey = MutableLiveData<Int>(0)
    fun setTemp(temp: String) {
        repository.setTemp(character.value?.id, temp)
    }

    fun heal(hp: String) {
        repository.heal(character.value?.id, hp, character.value!!.maxHp)
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
                val slot =
                    character.value!!.getAllSpellSlots().findLast { it.name == level.second }
                if ((slot?.currentAmount ?: 0) != 0 && spell.level <= level.first) {
                    result.add(level)
                }
            }
            result
        }
    }

    fun cast(spell: Spell, level: Int) {
        useSlot(level)
    }

    fun refundSlot(slot: Int) {
        if (
            !updatePactSlots(slot,+1) &&
            (character.value!!.spellSlots.getOrNull(slot - 1)?.currentAmount ?: 0)
            != (character.value!!.spellSlots.getOrNull(slot - 1)?.maxAmountType ?: "0").toInt()
        ) {
            character.value!!.spellSlots[slot - 1].currentAmount += 1
            repository.insertSpellSlots(character.value!!.spellSlots, character.value!!.id)
        }
    }

    fun useSlot(slot: Int) {
        if (!updatePactSlots(slot,-1) && (character.value!!.spellSlots.getOrNull(slot - 1)?.currentAmount ?: 0) != 0) {
            character.value!!.spellSlots[slot - 1].currentAmount -= 1
            repository.insertSpellSlots(character.value!!.spellSlots, character.value!!.id)
        }
    }

    private fun updatePactSlots(level: Int, amount: Int) : Boolean{
        character.value!!.classes.forEach { entry ->
            val slots=  entry.value.pactMagic?.pactSlots?.get(entry.value.level - 1)
            if(slots?.name == allSpellLevels[level - 1].second) {
                (slots.currentAmount.plus(amount)).let {
                    if (it <= slots.maxAmountType.toInt() && it >= 0) {
                        repository.insertPactMagicStateEntity(
                            characterId = character.value!!.id,
                            classId = entry.value.id,
                            slotsCurrentAmount = it
                        )
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getSpellSlotsAndCantrips(): MutableList<Resource> {
        val spellSlotsOffsetForCantrips = mutableListOf(
            Resource("Cantrip", 0, "0", "0")
        )
        character.value?.getAllSpellSlots()?.let { spellSlotsOffsetForCantrips.addAll(it) }
        return spellSlotsOffsetForCantrips
    }

    //Returns a list of booleans to spells
    //If the boolean is null the spell does not require preparation.
    //Else the boolean represents whether or not the spell is prepared.
    suspend fun getAllSpells(): Map<Int, List<Pair<Boolean?, Spell>>> {
        character.value?.let {
            return repository.getSpellsForCharacter(it)
        }
        return mapOf()
    }


    fun togglePreparation(spell: Spell, prepared: Boolean?) {
        for (item in character.value!!.classes.values) {
            if (
            //This class prepares spells.
                item.spellCasting?.prepareFrom != null &&
                //This class has access to the spell.
                spell.classes.contains(item.name.lowercase())
            ) {
                //If we have the spell prepared unprepared it.
                if (prepared == true) {
                    repository.insertCharacterClassSpellCrossRef(
                        spellId = spell.id,
                        characterId =character.value!!.id,
                        classId =item.id,
                        prepared = false
                    )
                } else {
                    //If this class can prepare more spells.
                    val totalPrepared = repository.getNumOfPreparedSpells(
                        classId = item.id,
                        characterId = character.value!!.id
                    )
                    if (
                        item.spellCasting.getMaxPrepared(
                            item.level,
                            character.value?.getStatMod(item.spellCasting.castingAbility) ?: 1
                        ) > totalPrepared
                    ) {
                        //Prepare the spell
                        repository.insertCharacterClassSpellCrossRef(
                            spellId = spell.id,
                            characterId =character.value!!.id,
                            classId =item.id,
                            prepared = true
                        )
                        break
                    }
                }
            }
        }
    }

    val character: MediatorLiveData<Character> = MediatorLiveData()


    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character,
            characterKey
        )
    }
}