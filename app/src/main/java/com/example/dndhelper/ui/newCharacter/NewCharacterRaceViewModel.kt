package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feature
import com.example.dndhelper.repository.dataClasses.Proficiency
import com.example.dndhelper.repository.dataClasses.Race
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
    var subraceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()
    var subraceASIDropdownState = mutableStateOf<MultipleChoiceDropdownState?>(null)
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
                    ?.getSelected(it.getAvailableOptions(character, proficiencies, character?.totalClassLevels ?: 0))
                        as List<Feature>
            }
        }

        if(!newRace.subraces.isNullOrEmpty()) {
            newRace.subrace = newRace.subraces[subraceIndex.value].run {
                this.traits.forEach {
                    if(it.choose.num(character?.totalClassLevels ?: 0) != 0 && it.options?.isNullOrEmpty() == false) {
                        it.chosen = subraceFeaturesDropdownStates[it.name + it.grantedAtLevel]
                            ?.getSelected(it.getAvailableOptions(character, proficiencies, character?.totalClassLevels ?: 0))
                                as List<Feature>
                    }
                }
                this
            }
        }

        character!!.race = newRace
        repository.insertCharacter(character)
    }

    val proficiencies: List<Proficiency>
        get() {
            //TODO update me.
            val profs: MutableList<Proficiency> = mutableListOf()

            return profs
        }

    fun filterRaceFeatures(
        race: Race,
    ): List<Feature> {
        val tempRace = race.copy()
        tempRace.subrace = tempRace.subraces?.get(subraceIndex.value)
        return tempRace.filterRaceFeatures()
    }
}

