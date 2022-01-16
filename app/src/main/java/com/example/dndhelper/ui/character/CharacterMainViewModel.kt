package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class CharacterMainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {



    var character : LiveData<Character>? = null

    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
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