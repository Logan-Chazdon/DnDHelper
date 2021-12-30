package com.example.dndhelper.ui.newCharacter.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.dndhelper.ui.newCharacter.MultipleChoiceDropdownState


fun SnapshotStateMap<String, MultipleChoiceDropdownState>.getDropDownState (
    key: String,
    maxSelections : Int,
    names : MutableList<String>,
    choiceName: String
) : MultipleChoiceDropdownState {

    val state : MultipleChoiceDropdownState =  if(this.containsKey(key)) {
        this[key]!!
    } else {
        generateDefault(
            maxSelections,
            names,
            choiceName
        )
    }

    this[key] = state

    return state
}

private fun generateDefault(
    maxSelections : Int,
    names : MutableList<String>,
    choiceName: String
): MultipleChoiceDropdownState {
    val result = MultipleChoiceDropdownState()
    result.maxSelections = maxSelections
    result.choiceName = choiceName
    result.names = names
    return result
}