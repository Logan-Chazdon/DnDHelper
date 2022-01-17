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
public class StatsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {


    var skills: LiveData<Map<String, List<String>>>? = repository.getSkillsByIndex("skill_proficiencies")

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

    fun toggleInspiration() {
        character?.value!!.inspiration = !character?.value!!.inspiration
        repository.insertCharacter(character!!.value!!)
    }

    fun checkForProficiencies(stats: List<String>): Map<String, Boolean>? {
        return character?.value?.checkForProficiencies(stats)
    }
}