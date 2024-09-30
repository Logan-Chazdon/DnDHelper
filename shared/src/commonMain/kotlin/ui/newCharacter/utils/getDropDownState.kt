package ui.newCharacter.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl


fun SnapshotStateMap<String, MultipleChoiceDropdownStateImpl>.getDropDownState (
    key: String,
    maxSelections : Int,
    names : MutableList<String>,
    costs: MutableList<Int> = mutableListOf(),
    choiceName: String,
    maxOfSameSelection: Int = 1
) : MultipleChoiceDropdownStateImpl {

    val state : MultipleChoiceDropdownStateImpl =  if(this.containsKey(key)) {
        this[key]!!.names = names
        this[key]!!.maxSelections = maxSelections
        this[key]!!
    } else {
        generateDefault(
            maxSelections,
            names,
            choiceName,
            maxOfSameSelection,
            costs
        )
    }

    this[key] = state

    return state
}

fun SnapshotStateList<MultipleChoiceDropdownStateImpl>.getDropDownState (
    key: Int,
    maxSelections : Int,
    names : MutableList<String>,
    costs: MutableList<Int> = mutableListOf(),
    choiceName: String,
    maxOfSameSelection: Int = 1
) : MultipleChoiceDropdownStateImpl {
    if(key > this.size) {
        for(i in 0..key) {
            if(this.elementAtOrNull(i) == null) {
                this.add(
                    i,
                     generateDefault(
                        maxSelections,
                        names,
                        choiceName,
                        maxOfSameSelection,
                         costs
                     )
                )
            }
        }
    }

    val state : MultipleChoiceDropdownStateImpl
    if(this.elementAtOrNull(key) != null) {
        this[key].maxSelections = maxSelections
        state = this[key]
    } else {
        state = generateDefault(
            maxSelections,
            names,
            choiceName,
            maxOfSameSelection,
            costs
        )
        this.add(key, state)
    }
    return state
}

private fun generateDefault(
    maxSelections: Int,
    names: MutableList<String>,
    choiceName: String,
    maxOfSameSelection: Int,
    costs: MutableList<Int>
): MultipleChoiceDropdownStateImpl {
    val result = MultipleChoiceDropdownStateImpl()
    result.maxSelections = maxSelections
    result.choiceName = choiceName
    result.names = names
    result.maxSameSelections = maxOfSameSelection
    result.costs = costs
    return result
}