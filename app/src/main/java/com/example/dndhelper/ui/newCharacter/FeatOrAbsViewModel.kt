package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class FeatOrAbsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    application: Application,
):  AndroidViewModel(application) {
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

    val featOrAbsNum: Int
    get() {
        when(character?.totalClassLevels) {
            in 0..4 -> {
                return 1
            }
            in 5..8 -> {
                return 2
            }
            in 9..12 -> {
                return 3
            }
            in  13..16 -> {
                return 4
            }
            in  17..19 -> {
                return 5
            }
        }
        return 0
    }


    var character: Character? = null
    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getCharacterById(id)
            feats = repository.getFeats()

            featNames.addSource(feats!!) {
                val names = mutableListOf<String>()
                for(item in it) {
                    names.add(item.name)
                }
                featNames.value = names
            }
        }


    }

}