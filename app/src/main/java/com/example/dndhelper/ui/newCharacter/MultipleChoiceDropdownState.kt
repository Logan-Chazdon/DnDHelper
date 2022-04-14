package com.example.dndhelper.ui.newCharacter

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class MultipleChoiceDropdownState()  {
    val selectedList =  mutableStateListOf<Int>()
    private val _selectedNames = MutableLiveData("")
    val selectedNames: LiveData<String> = _selectedNames
    var maxSameSelections = 1
    var maxSelections = 0

    var names : List<String> = listOf()
        set(newName: List<String>) {
            field = newName
            //This fills selectedList with false and makes sure its the correct size
            for (i in names.indices) {
                if (i >= selectedList.size) {
                    selectedList.add(i, 0)
                }
            }
        }

    var choiceName = ""
        set(newName : String) {
            field = newName
            if(_selectedNames.value == "")
                _selectedNames.value = newName
        }



    fun incrementSelection(index:Int) {
        //Change the selection and make sure we stay below the max selections.
        var selections = 0
        for(item in selectedList) {
            selections += item
        }

        if(selections >= maxSelections) {
            val firstIndex = selectedList.indexOfFirst{it -> it != 0}
            selectedList[firstIndex] = selectedList[firstIndex] - 1
        }
        selectedList[index] = selectedList[index] + 1

        //Update the name to only show the selected options.
        var newNames = ""
        for(i in names.indices) {
            if(selectedList[i] > 0) {
                newNames += names[i] + " "
            }
        }
        _selectedNames.value = (newNames)
    }

    fun decrementSelection(index: Int) {
        if(selectedList[index] != 0)
             selectedList[index] = selectedList[index] - 1
    }

    //This allows the owner of the view to pass in a list of choices and get back only what is selected.
    fun getSelected(from : List<Any>) : List<Any>{
        val returnList = mutableListOf<Any>()
        for(i in from.indices) {
            if(selectedList[i] != 0) {
                if(maxSameSelections == 1) {
                    returnList.add(from[i])
                } else {
                    returnList.add(Pair(from[i], selectedList[i]))
                }
            }
        }

        return returnList
    }

    fun setSelected(selectedNames : List<String>) {
        selectedNames.forEach {
            incrementSelection(names.indexOf(it))
        }
    }

    @JvmName("setSelectedByIndex")
    fun setSelected(selectedIndexes : List<Int>)  {
        selectedIndexes.forEach {
            incrementSelection(it)
        }
    }
}