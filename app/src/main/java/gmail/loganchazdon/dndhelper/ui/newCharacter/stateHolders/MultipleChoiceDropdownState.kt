package gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders

import androidx.lifecycle.LiveData

interface MultipleChoiceDropdownState {
    val choiceName : String
    val selectedList : List<Int>
    val selectedNames: LiveData<String>
    val names: List<String>
    val maxSelections: Int
    val subChoiceKeys: List<String>?
    var costs : List<Int>

    fun getSubChoiceAt(key : String) : MultipleChoiceDropdownState?
    fun incrementSelection(index:Int)
    fun decrementSelection(index: Int)
    fun getMaxSameSelectionsAt(index : Int): Int
}