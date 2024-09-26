package ui.character

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import model.Armor
import model.Character
import model.ItemInterface
import model.Shield
import model.repositories.CharacterRepository
import model.repositories.ItemRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
public class ItemViewModel constructor(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository,
    private val itemRepository: ItemRepository,
    application: Application
) : AndroidViewModel(application) {
    var character: MediatorLiveData<Character> = MediatorLiveData()
    var allItems: LiveData<List<ItemInterface>>? = null

    init {
        characterRepository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )
        viewModelScope.launch {
            allItems = itemRepository.getAllItems()

        }
    }

    suspend fun addItem(item: ItemInterface) {
        character.value?.backpack?.addItem(
            item
        )
        character.value?.let { newCharacter ->
            characterRepository.insertCharacter(newCharacter)
        }
    }

    suspend fun buyItem(item: ItemInterface) {
        val cost = item.cost
        if (character.value?.backpack?.subtractCurrency(cost!!) == true) {
            addItem(item)
            characterRepository.insertCharacter(character.value!!)
        }
    }

    suspend fun addCurrency(name: String?, newAmount: Int) {
        var nonAddedCurrency: Int =
            character.value?.backpack!!.backgroundCurrency[name]?.copy()?.amount ?: 0
        nonAddedCurrency += character.value?.backpack!!.classCurrency[name]?.copy()?.amount ?: 0
        character.value?.backpack!!.addedCurrency[name]!!.amount = newAmount - nonAddedCurrency

        character.value?.let {
            characterRepository.insertCharacter(it)
        }
    }

     fun equip(armor: Armor) {
        character.value?.backpack?.equippedArmor = armor
        characterRepository.insertCharacter(character.value!!)
    }

    fun equip(shield: Shield) {
        character.value?.backpack?.equippedShield = shield
        characterRepository.insertCharacter(character.value!!)
    }

    suspend fun deleteItemAt(itemToDeleteIndex: Int) {
        character.value?.backpack?.deleteItemAtIndex(itemToDeleteIndex)
        characterRepository.insertCharacter(character.value!!)
    }
}