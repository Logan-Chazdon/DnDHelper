package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class CharacterMainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {


    val characterFeatures: MediatorLiveData<List<Feature>> = MediatorLiveData()

    var character : LiveData<Character>? = null

    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
            characterFeatures.addSource(character!!) {
                characterFeatures.value = it.features
            }
        }

    }

    fun setName(it: String) {
        val newChar = character!!.value!!.copy(name = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
     }

    fun longRest() {
        val newChar = character!!.value!!.copy()
        newChar.id = character!!.value!!.id
        newChar.longRest()
        repository.insertCharacter(newChar)
    }

    fun shortRest() {
      //TODO
    }

}