package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CharacterMainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: CharacterRepository,
    application: Application
): AndroidViewModel(application) {
    private val debounceTime: Long = 1000
    val characterFeatures: MediatorLiveData<List<Pair<Int, Feature>>> = MediatorLiveData()
    var character : MediatorLiveData<Character> = MediatorLiveData()

    //Character Information.
    val name = MutableStateFlow("")
    val ideals = MutableStateFlow("")
    val personalityTraits = MutableStateFlow("")
    val bonds = MutableStateFlow("")
    val flaws = MutableStateFlow("")
    val notes = MutableStateFlow("")

    private val dataToPersistenceFunction = mapOf(
        name to ::setName,
        personalityTraits to ::setPersonalityTraits,
        bonds to ::setBonds,
        flaws to ::setFlaws,
        notes to ::setNotes,
        ideals to ::setIdeals
    )

    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )

        characterFeatures.addSource(character) {
            characterFeatures.value = it.displayFeatures
        }

        //Call the persistence function of state flow after the user types
        //and then doesn't type for debounce time.
        for (it in dataToPersistenceFunction) {
            viewModelScope.launch(Dispatchers.IO) {
                it.key.debounce(debounceTime)
                    .collect(it.value)
            }
        }
    }



    fun longRest() {
        character.value!!.longRest()
        repository.insertCharacter(character.value!!)
    }

    fun shortRest() {
      //TODO
    }

    private fun setName(it: String) {
        repository.changeName(it, character.value!!.id)
    }

    private fun setPersonalityTraits(it: String) {
        repository.setPersonalityTraits(it, character.value!!.id)
    }

    private fun setIdeals(it: String) {
        repository.setIdeals(it, character.value!!.id)
    }

    private fun setBonds(it: String) {
        repository.setBonds(it, character.value!!.id)
    }

    private fun setFlaws(it: String) {
        repository.setFlaws(it, character.value!!.id)
    }

    private fun setNotes(it: String) {
        repository.setNotes(it, character.value!!.id)
    }

    fun infuse(targetItem: ItemInterface?, infusion: Infusion) {
        /*val newChar = activateInfusion(infusion, character.value!!.copy())
        if(targetItem != null) {
            newChar?.backpack?.applyInfusion(targetItem, infusion)
        }
        newChar?.id = character.value!!.id
        newChar?.let { character -> repository.insertCharacter(character) }*/
    }

    fun disableInfusion(infusion: Infusion) {
        /*val newChar = deactivateInfusion(infusion, character.value!!.copy())
        newChar?.backpack?.removeInfusion(infusion)
        newChar?.id = character.value!!.id
        newChar?.let { character -> repository.insertCharacter(character) }*/
    }

    //This function just returns the character passed in with the target infusion set to active or null.
    private fun activateInfusion(infusion: Infusion, character: Character) : Character? {
        //TODO check for other possible sources of infusions.
        character.classes.values.forEachIndexed { classIndex, clazz  ->
            clazz.levelPath!!.forEachIndexed { index, it ->
                if (it.grantsInfusions) {
                    if(character.classes.values.elementAt(classIndex).levelPath!![index].activateInfusion(infusion))
                        return character
                }
            }
        }
        return null
    }

    //Attempt to find an active infusion equal to the one passed and disable it.
    fun deactivateInfusion(infusion: Infusion, character: Character): Character? {
        //TODO check for other possible sources of infusions.
        character.classes.values.forEachIndexed { classIndex, clazz  ->
            clazz.levelPath!!.forEachIndexed { index, it ->
                if (it.grantsInfusions) {
                    if(character.classes.values.elementAt(classIndex).levelPath!![index].deactivateInfusion(infusion))
                        return character
                }
            }
        }
        return null
    }
}