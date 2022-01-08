package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dndhelper.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import com.example.dndhelper.repository.dataClasses.Character


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
         character?.value!!.name = it
         repository.insertCharacter(character!!.value!!)
    }

}