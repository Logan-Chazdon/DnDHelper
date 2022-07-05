package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Armor
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.Shield
import gmail.loganchazdon.dndhelper.model.Weapon
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(FlowPreview::class)
@HiltViewModel
public class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {
    private val debounceTime:Long = 1000
    val character = repository.getLiveCharacterById(savedStateHandle.get<String>("characterId")!!.toInt())!!
    private val itemIndex: Int = savedStateHandle.get<String>("itemIndex")!!.toInt()
    //Mediator live data that gets an item from the characters backpack by index.
    val item = MediatorLiveData<ItemInterface>().run {
        this.addSource(character) {
            this.value = it.backpack.getItemByIndex(itemIndex)
        }
        this.value = character.value?.backpack?.getItemByIndex(itemIndex)
        this
    }

    //Data to show to the user and be edited.
    val itemDesc = MutableStateFlow("")
    val itemName = MutableStateFlow("")
    val armorBaseAc = MutableStateFlow("")
    val shieldBaseAc = MutableStateFlow("")
    val weaponDamage = MutableStateFlow("")
    val armorStealthDisadvantage = MutableStateFlow(false)

    init {
        //Update the db if the user has not edited any of the values in the last debounceTime millis.
        viewModelScope.launch(Dispatchers.IO) {
            itemName.debounce(debounceTime)
                .collect(::updateItemName)
        }

        viewModelScope.launch(Dispatchers.IO) {
            itemDesc.debounce(debounceTime)
                .collect(::updateItemDesc)
        }

        viewModelScope.launch(Dispatchers.IO) {
            armorBaseAc.debounce(debounceTime)
                .collect(::updateArmorBaseAc)
        }

        viewModelScope.launch(Dispatchers.IO) {
            shieldBaseAc.debounce(debounceTime)
                .collect(::updateShieldBaseAc)
        }

        viewModelScope.launch(Dispatchers.IO) {
            weaponDamage.debounce(debounceTime)
                .collect(::updateWeaponDamage)
        }

        viewModelScope.launch(Dispatchers.IO) {
            armorStealthDisadvantage.debounce(debounceTime)
                .collect(::updateArmorStealthDisadvantage)
        }
    }


    private fun updateItem() {
        val tempChar = character.value?.copy()
        item.value?.let { tempChar?.backpack?.replaceItemAt(it, itemIndex) }
        tempChar?.let { repository.insertCharacter(it) }
    }


    private fun updateItemName(value: String) {
        item.value?.name = value
        updateItem()
    }

    private fun updateItemDesc(value: String) {
        item.value?.desc = value
        updateItem()
    }

    private fun updateArmorBaseAc(value: String) {
        if(item.value is Armor) {
            (item.value as Armor).baseAc = value.toInt()
            updateItem()
        }
    }

    private fun updateShieldBaseAc(value: String) {
        if(item.value is Shield) {
            (item.value as Shield).acBonus = value.toInt()
            updateItem()
        }
    }

    private fun updateWeaponDamage(value: String) {
        if(item.value is Weapon) {
            (item.value as Weapon).damage = value
            updateItem()
        }
    }


    private fun updateArmorStealthDisadvantage(value: Boolean) {
        if(item.value is Armor) {
            (item.value as Armor).stealth = if(value) {""} else {"Disadvantage"}
            updateItem()
        }
    }


    //In case the viewModel is cleared early save all changes.
    override fun onCleared() {
        CoroutineScope(Dispatchers.IO).launch {
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