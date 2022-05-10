package com.example.dndhelper.ui.newCharacter.stateHolders

import androidx.lifecycle.LiveData

interface MultipleChoiceDropdownState {
    val choiceName : String
    val selectedList : List<Int>
    val selectedNames: LiveData<String>
    val names: List<String>
    val maxSelections: Int

    fun incrementSelection(index:Int)
    fun decrementSelection(index: Int)
    fun getMaxSameSelectionsAt(index : Int): Int
}