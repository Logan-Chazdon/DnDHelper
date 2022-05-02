package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.*
import com.example.dndhelper.ui.newCharacter.utils.getFeatsAt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
public class NewCharacterRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    lateinit var character: LiveData<Character>
    var raceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    val raceProficiencyChoiceDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    var subraceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    var subraceASIDropdownState = mutableStateOf<MultipleChoiceDropdownState?>(null)
    val subraceFeatDropdownStates = mutableStateListOf<MultipleChoiceDropdownState>()
    val subraceFeatChoiceDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    val languageDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    lateinit var races: LiveData<List<Race>>
    var id by Delegates.notNull<Int>()
    var raceIndex = 0
    var subraceIndex = mutableStateOf(0)

    init {
        try {
            id = savedStateHandle.get<String>("characterId")!!.toInt()
            character = repository.getLiveCharacterById(id)!!
        } catch (E: Exception) {
            id = -1
        }


        viewModelScope.launch {
            races = repository.getRaces()
        }
    }

    suspend fun setRace(newRace: Race) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)

        filterRaceFeatures(newRace).forEach {
            if(it.choose.num(character?.totalClassLevels ?: 0) != 0 && it.options?.isNullOrEmpty() == false) {
                it.chosen = raceFeaturesDropdownStates[it.name + it.grantedAtLevel]
                    ?.getSelected(it.getAvailableOptions(
                        character,
                        proficiencies,
                        character?.totalClassLevels ?: 0,
                        null,
                        calculateAssumedSpells(),
                        calculateAssumedStatBonuses()
                    ))
                        as List<Feature>
            }
        }

        if (newRace.subrace?.languages.isNullOrEmpty() &&
            newRace.subrace?.languageChoices.isNullOrEmpty()
        )
        newRace.languageChoices.forEach {
            it.chosen = languageDropdownStates[it.name]
                ?.getSelected(it.from) as List<Language>
        }

        newRace.proficiencyChoices.forEach {
            it.chosen = raceProficiencyChoiceDropdownStates[it.name]
                ?.getSelected(it.from) as List<Proficiency>
        }

        if(!newRace.subraces.isNullOrEmpty()) {
            newRace.subrace = newRace.subraces[subraceIndex.value].run {
                this.traits.forEach {
                    if(it.choose.num(character?.totalClassLevels ?: 0) != 0 && it.options?.isNullOrEmpty() == false) {
                        it.chosen = subraceFeaturesDropdownStates[it.name + it.grantedAtLevel]
                            ?.getSelected(it.getAvailableOptions(
                                character,
                                proficiencies,
                                character?.totalClassLevels ?: 0,
                                null,
                                calculateAssumedSpells(),
                                calculateAssumedStatBonuses()
                            ))
                                as List<Feature>
                    }
                }

                this.languageChoices.forEach {
                    it.chosen = languageDropdownStates[it.name]
                        ?.getSelected(it.from) as List<Language>
                }

                selectedSubraceASIs.let { result ->
                    val chosen = mutableListOf<AbilityBonus>()
                    result.forEach {
                        chosen.add(
                            AbilityBonus(
                                ability = it.first,
                                bonus= it.second
                            )
                        )
                    }
                    this.abilityBonusChoice?.chosen = chosen
                }

                featChoices?.let { choices ->
                    subraceFeatDropdownStates.forEachIndexed { index, _ ->
                        choices[index].chosen = getFeatsAt(
                            index,
                            1,
                            subraceFeatDropdownStates,
                            subraceFeatChoiceDropDownStates,
                            choices[index].from
                        )
                    }
                }

                this
            }
        }

        character!!.race = newRace
        repository.insertCharacter(character)
    }

    private val selectedSubraceASIs : List<Pair<String, Int>>
        get() {
            return ((subraceASIDropdownState.value?.getSelected(
                races.value?.get(raceIndex)?.subraces?.get(subraceIndex.value)
                    ?.abilityBonusChoice?.from.let { from ->
                        val names = mutableListOf<Pair<String, Int>>()
                        from?.forEach { it ->
                            names.add(Pair(it.ability, it.bonus))
                        }
                        names
                    }) ?: listOf<Pair<String, Int>>()) as List<Pair<String, Int>>)
        }

    val proficiencies: List<Proficiency>
        get() {
            //TODO update me.
            val profs: MutableList<Proficiency> = mutableListOf()

            return profs
        }

    fun calculateAssumedSpells(): MutableList<Spell> {
        val result = mutableListOf<Spell>()
        character.value?.let { repository.getSpellsForCharacter(it) }?.let {
            it.forEach { (_, spells) ->
                spells.forEach { (_, spell) ->
                    result.add(spell)
                }
            }
        }
        //TODO spells from feats
        return result
    }

    fun calculateAssumedStatBonuses(): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        val applyBonus = fun(name: String, amount: Int) {
            result[name.substring(0, 3)] =
                (result[name.substring(0, 3)] ?: 0) + amount
        }

        races.value?.getOrNull(raceIndex)?.abilityBonuses?.forEach {
            applyBonus(it.ability, it.bonus)
        }

        races.value?.get(raceIndex)?.subraces?.getOrNull(subraceIndex.value)?.abilityBonuses?.forEach {
            applyBonus(it.ability, it.bonus)
        }

        selectedSubraceASIs.forEach {
            applyBonus(it.first, it.second)
        }

        //TODO stats from feats
        return result
    }

    fun filterRaceFeatures(
        race: Race,
    ): List<Feature> {
        val tempRace = race.copy()
        tempRace.subrace = tempRace.subraces?.getOrNull(subraceIndex.value)
        return tempRace.filterRaceFeatures()
    }
}

