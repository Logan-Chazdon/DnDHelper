package com.example.dndhelper.ui.newCharacter

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//This viewModel is used on the MultipleChoiceDropdownView UI Component.
//We're using a viewModel here to make it easier to add more than one of this UI component without code duplication.
class MultipleChoiceDropdownViewModel() : ViewModel() {
    val selectedList =  mutableStateListOf<Boolean>()
    private val _selectedNames = MutableLiveData("")
    val selectedNames: LiveData<String> = _selectedNames

    var maxSelections = 0
    var names : List<String> = listOf()
        set(newName: List<String>) {
            field = newName
            //This fills selectedList with false and makes sure its the correct size
            for (i in names.indices) {
                if (i >= selectedList.size) {
                    selectedList.add(i, false)
                }
            }
        }

    var choiceName = ""
        set(newName : String) {
            field = newName
            if(_selectedNames.value == "")
                _selectedNames.value = newName
        }



    fun changeSelection(index:Int) {
        //Change the selection and make sure we stay below the max selections.
        val selections = selectedList.count { Boolean -> Boolean }
        if(selections >= maxSelections && !selectedList[index]) {
            selectedList[selectedList.indexOf(true)] = false
        }
        selectedList[index] = !selectedList[index]

        //Update the name to only show the selected options.
        var newNames = ""
        for(i in names.indices) {
            if(selectedList[i]) {
                newNames += names[i] + " "
            }
        }
        _selectedNames.value = (newNames)

        //If they haven't selected anything change the name to choiceName
       // if(_selectedNames.value == "")
       //     _selectedNames.value = choiceName
    }


}