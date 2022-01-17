package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Armor
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.ItemInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var character: LiveData<Character>? = null
    var allItems: LiveData<List<ItemInterface>>? = null


    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
            allItems = repository.getAllItems()

        }

    }


    suspend fun addItem(item: ItemInterface) {
        character?.value?.backpack?.addItem(
            item
        )
        character?.value.let { newCharacter ->
            if (newCharacter != null) {
                repository.insertCharacter(newCharacter)
            }
        }
    }

    suspend fun buyItem(item: ItemInterface) {
        val cost = item.cost
        if (character?.value?.backpack?.subtractCurrency(cost!!) == true) {
            addItem(item)
            val newChar = character!!.value!!.copy(backpack = character!!.value!!.backpack)
            newChar.id = character!!.value!!.id
            repository.insertCharacter(newChar)
        }
    }

    suspend fun addCurrency(name: String?, newAmount: Int) {
        var nonAddedCurrency: Int =
            character?.value?.backpack!!.backgroundCurrency[name]?.copy()?.amount ?: 0
        nonAddedCurrency += character?.value?.backpack!!.classCurrency[name]?.copy()?.amount ?: 0
        character?.value?.backpack!!.addedCurrency[name]!!.amount = newAmount - nonAddedCurrency

        character?.value?.let {
            repository.insertCharacter(it)
        }
    }

    suspend fun equip(armor: Armor): Boolean {
        return if (character?.value?.getStat("Str") ?: 0 >= armor.strengthPrerequisite ?: 0) {
            character?.value?.equiptArmor = armor
            repository.insertCharacter(character?.value!!)
            true
        } else {
            false
        }
    }

    suspend fun deleteItemAt(itemToDeleteIndex: Int) {
        character?.value?.backpack?.deleteItemAtIndex(itemToDeleteIndex)
        repository.insertCharacter(character?.value!!)
    }

}