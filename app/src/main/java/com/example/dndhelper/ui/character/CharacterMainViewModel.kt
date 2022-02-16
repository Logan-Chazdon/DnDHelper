package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feature
import com.example.dndhelper.repository.dataClasses.Infusion
import com.example.dndhelper.repository.dataClasses.ItemInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class CharacterMainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {


    val characterFeatures: MediatorLiveData<List<Pair<Int, Feature>>> = MediatorLiveData()

    var character : LiveData<Character>? = null

    init {
        val id = savedStateHandle.get<String>("characterId")!!.toInt()

        viewModelScope.launch {
            character = repository.getLiveCharacterById(id)
            characterFeatures.addSource(character!!) {
                characterFeatures.value = it.features
            }
        }

    }



    fun longRest() {
        val newChar = character!!.value!!.copy()
        newChar.id = character!!.value!!.id
        newChar.longRest()
        repository.insertCharacter(newChar)
    }

    fun shortRest() {
      //TODO
    }

    fun setName(it: String) {
        val newChar = character!!.value!!.copy(name = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun setPersonalityTraits(it: String) {
        val newChar = character!!.value!!.copy(personalityTraits = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun setIdeals(it: String) {
        val newChar = character!!.value!!.copy(ideals = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun setBonds(it: String) {
        val newChar = character!!.value!!.copy(bonds = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun setFlaws(it: String) {
        val newChar = character!!.value!!.copy(flaws = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun setNotes(it: String) {
        val newChar = character!!.value!!.copy(notes = it)
        newChar.id = character!!.value!!.id
        repository.insertCharacter(newChar)
    }

    fun infuse(targetItem: ItemInterface?, infusion: Infusion) {
        val newChar = activateInfusion(infusion, character!!.value!!.copy())
        if(targetItem != null) {
            newChar?.backpack?.applyInfusion(targetItem, infusion)
        }
        newChar?.id = character!!.value!!.id
        newChar?.let { character -> repository.insertCharacter(character) }
    }

    //This function just returns the character passed in with the target infusion set to active or null.
    private fun activateInfusion(infusion: Infusion, character: Character) : Character? {
        //TODO check for other possible sources of infusions.
        character.classes.values.forEachIndexed { classIndex, clazz  ->
            clazz.levelPath.forEachIndexed { index, it ->
                if (it.grantsInfusions) {
                    if(character.classes.values.elementAt(classIndex).levelPath[index].activateInfusion(infusion))
                        return character
                }
            }
        }
        return null
    }


}