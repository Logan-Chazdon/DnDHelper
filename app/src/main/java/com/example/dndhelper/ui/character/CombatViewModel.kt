package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltViewModel
public class CombatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {

    fun addTemp(temp: String) {
        val tempChar = character!!.value!!.copy(tempHp = character!!.value!!.tempHp + temp.toInt())
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

    fun damage(temp: String) {

        var currentHp = 0
        var tempHp = 0

        val tempChar = character!!.value!!.copy(currentHp = currentHp, tempHp = tempHp)
        tempChar.id = character!!.value!!.id

        repository.insertCharacter(tempChar)
    }

    var character : LiveData<Character>? = null


    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()
        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
        }

    }
}