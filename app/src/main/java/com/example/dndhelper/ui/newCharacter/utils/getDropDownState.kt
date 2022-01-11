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

fun SnapshotStateList<MultipleChoiceDropdownState>.getDropDownState (
    key: Int,
    maxSelections : Int,
    names : MutableList<String>,
    choiceName: String
) : MultipleChoiceDropdownState {
    if(key > this.size) {
        for(i in 0..key) {
            if(this.elementAtOrNull(i) == null) {
                this.add(
                    i,
                     generateDefault(
                        maxSelections,
                        names,
                        choiceName
                     )
                )
            }
        }
    }

    val state : MultipleChoiceDropdownState
    if(this.elementAtOrNull(key) != null) {
        state = this[key]
    } else {
        state = generateDefault(
            maxSelections,
            names,
            choiceName
        )
        this.add(key, state)
    }
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