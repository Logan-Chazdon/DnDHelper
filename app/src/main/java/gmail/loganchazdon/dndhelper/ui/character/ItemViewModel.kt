package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Armor
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.Shield
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var character: MediatorLiveData<Character> = MediatorLiveData()
    var allItems: LiveData<List<ItemInterface>>? = null


    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )
        viewModelScope.launch {
            allItems = repository.getAllItems()

        }

    }


    suspend fun addItem(item: ItemInterface) {
        character.value?.backpack?.addItem(
            item
        )
        character.value.let { newCharacter ->
            if (newCharacter != null) {
                repository.insertCharacter(newCharacter)
            }
        }
    }

    suspend fun buyItem(item: ItemInterface) {
        val cost = item.cost
        if (character.value?.backpack?.subtractCurrency(cost!!) == true) {
            addItem(item)
            val newChar = character.value!!.copy(backpack = character.value!!.backpack)
            newChar.id = character.value!!.id
            repository.insertCharacter(newChar)
        }
    }

    suspend fun addCurrency(name: String?, newAmount: Int) {
        var nonAddedCurrency: Int =
            character.value?.backpack!!.backgroundCurrency[name]?.copy()?.amount ?: 0
        nonAddedCurrency += character.value?.backpack!!.classCurrency[name]?.copy()?.amount ?: 0
        character.value?.backpack!!.addedCurrency[name]!!.amount = newAmount - nonAddedCurrency

        character.value?.let {
            repository.insertCharacter(it)
        }
    }

     fun equip(armor: Armor) {
        character.value?.backpack?.equippedArmor = armor
        repository.insertCharacter(character.value!!)
    }

    fun equip(shield: Shield) {
        character.value?.backpack?.equippedShield = shield
        repository.insertCharacter(character.value!!)
    }

    suspend fun deleteItemAt(itemToDeleteIndex: Int) {
        character.value?.backpack?.deleteItemAtIndex(itemToDeleteIndex)
        repository.insertCharacter(character.value!!)
    }

}