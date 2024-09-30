package ui.character


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.transformLatest
import model.Armor
import model.Character
import model.Shield
import model.Weapon
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@OptIn(FlowPreview::class)
@KoinViewModel
public class ItemDetailsViewModel constructor(
    savedStateHandle: SavedStateHandle,
    val repository: CharacterRepository
): ViewModel() {
    private val debounceTime:Long = 1000
    val character = MutableStateFlow<Character>(Character())
    private val itemIndex: Int = savedStateHandle.get<String>("itemIndex")!!.toInt()
    //Mediator live data that gets an item from the characters backpack by index.
    @OptIn(ExperimentalCoroutinesApi::class)
    val item = character.transformLatest { value ->
        if(value.backpack.allItems.isNotEmpty()) emit(value.backpack.getItemByIndex(itemIndex))
    }

    //Data to show to the user and be edited.
    val itemDesc = MutableStateFlow("")
    val itemName = MutableStateFlow("")
    val armorBaseAc = MutableStateFlow("")
    val shieldBaseAc = MutableStateFlow("")
    val weaponDamage = MutableStateFlow("")
    val armorStealthDisadvantage = MutableStateFlow(false)

    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )

        //Update the db if the user has not edited any of the values in the last debounceTime millis.
        viewModelScope.launch() {
            itemName.debounce(debounceTime)
                .collect(::updateItemName)
        }

        viewModelScope.launch() {
            itemDesc.debounce(debounceTime)
                .collect(::updateItemDesc)
        }

        viewModelScope.launch() {
            armorBaseAc.debounce(debounceTime)
                .collect(::updateArmorBaseAc)
        }

        viewModelScope.launch() {
            shieldBaseAc.debounce(debounceTime)
                .collect(::updateShieldBaseAc)
        }

        viewModelScope.launch() {
            weaponDamage.debounce(debounceTime)
                .collect(::updateWeaponDamage)
        }

        viewModelScope.launch() {
            armorStealthDisadvantage.debounce(debounceTime)
                .collect(::updateArmorStealthDisadvantage)
        }
    }


    private suspend fun updateItem() {
        val tempChar = character.value
        item.last()?.let { tempChar?.backpack?.replaceItemAt(it, itemIndex) }
        tempChar?.let { repository.insertCharacter(it) }
    }


    private suspend fun updateItemName(value: String) {
        item.last()?.name = value
        updateItem()
    }

    private suspend fun updateItemDesc(value: String) {
        item.last()?.desc = value
        updateItem()
    }

    private suspend fun updateArmorBaseAc(value: String) {
        if(item.last() is Armor) {
            (item.last() as Armor).baseAc = value.toInt()
            updateItem()
        }
    }

    private suspend fun updateShieldBaseAc(value: String) {
        if(item.last() is Shield) {
            (item.last() as Shield).acBonus = value.toInt()
            updateItem()
        }
    }

    private suspend fun updateWeaponDamage(value: String) {
        if(item.last() is Weapon) {
            (item.last() as Weapon).damage = value
            updateItem()
        }
    }


    private suspend fun updateArmorStealthDisadvantage(value: Boolean) {
        if(item.last() is Armor) {
            (item.last() as Armor).stealth = if(value) {""} else {"Disadvantage"}
            updateItem()
        }
    }


    //In case the viewModel is cleared early save all changes.
    override fun onCleared() {
        CoroutineScope(Dispatchers.Default).launch {
            updateShieldBaseAc(shieldBaseAc.value)
            updateArmorBaseAc(armorBaseAc.value)
            updateItemName(itemName.value)
            updateItemDesc(itemDesc.value)
            updateShieldBaseAc(shieldBaseAc.value)
            updateWeaponDamage(weaponDamage.value)
            updateArmorStealthDisadvantage(armorStealthDisadvantage.value)
        }
        super.onCleared()
    }
}