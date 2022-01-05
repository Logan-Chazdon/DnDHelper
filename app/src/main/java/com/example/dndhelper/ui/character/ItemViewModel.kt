package com.example.dndhelper.ui.character

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {

    var character : LiveData<Character>? = null
    var allItems : LiveData<List<ItemInterface>>? = null


    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
            allItems = repository.getAllItems()

        }

    }



    suspend fun addItem(selected: Int) {
        allItems?.value?.let {
            character?.value?.backpack?.addItem(
                it[selected]
            )
            character?.value.let { newCharacter ->
                if (newCharacter != null) {
                    repository.insertCharacter(newCharacter)
                }
            }
        }
    }

    suspend fun buyItem(selected: Int) {
        val cost = allItems?.value?.get(selected)?.cost
        if(character?.value?.backpack?.subtractCurrency(cost!!) == true) {
            addItem(selected)
        }

    }

    suspend fun addCurrency(name: String?, newAmount: Int) {
        var nonAddedCurrency : Int = character?.value?.backpack!!.backgroundCurrency[name]?.copy()?.amount ?: 0
        nonAddedCurrency += character?.value?.backpack!!.classCurrency[name]?.copy()?.amount ?: 0
        character?.value?.backpack!!.addedCurrency[name]!!.amount = newAmount - nonAddedCurrency

        character?.value?.let{
            repository.insertCharacter(it)
        }
    }

    suspend fun equip(armor: Armor) {
        character?.value?.equiptArmor = armor
        repository.insertCharacter(character?.value!!)
    }

}