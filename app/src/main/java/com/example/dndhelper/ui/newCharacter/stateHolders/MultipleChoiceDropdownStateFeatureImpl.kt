package com.example.dndhelper.ui.newCharacter.stateHolders

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.model.*


class MultipleChoiceDropdownStateFeatureImpl(
    val feature: Feature
) : MultipleChoiceDropdownState {
    var character: Character? = null
    var assumedProficiencies: List<Proficiency> = listOf()
    var level: Int = 1
    var assumedClass: Class? = null
    var assumedSpells: List<Spell> = listOf()
    var assumedStatBonuses: Map<String, Int>? = null
    var assumedFeatures: List<Feature> = listOf()

    override val selectedNames: MutableLiveData<String> = MutableLiveData(feature.name)

    override var choiceName = ""
        set(newName) {
            field = newName
            if(selectedNames.value == "")
                selectedNames.value = newName
        }

    private val selectedFeatures: SnapshotStateMap<String, Int> = mutableStateMapOf()
    override val selectedList:  SnapshotStateList<Int>
    get() {
        val result = mutableStateListOf<Int>()
        options.forEach {
            result.add(selectedFeatures[it.name] ?: 0)
        }
        return  result
    }
    override val names: List<String>
        get() {
            val result = mutableListOf<String>()
            options.forEach {
                result.add(it.name)
            }
            return result
        }

    val options : List<Feature>
    get() {
        return feature.getAvailableOptions(
            character = character,
            assumedFeatures = assumedFeatures,
            assumedSpells = assumedSpells,
            assumedClass = assumedClass,
            assumedStatBonuses = assumedStatBonuses,
            assumedProficiencies = assumedProficiencies,
            level = level
        )
    }

    override val maxSelections: Int
        get() = feature.choose.num(level)


    override fun incrementSelection(index: Int) {
        //Change the selection and make sure we stay below the max selections.
        var selections = 0
        for(item in selectedList) {
            selections += item
        }

        if(selections >= maxSelections) {
            val firstIndex = selectedList.indexOfFirst{ it != 0}
            selectedFeatures[options[firstIndex].name]=
                selectedFeatures[options[firstIndex].name]?.minus(1) ?: 0
        }

        selectedFeatures[options[index].name]=
            selectedFeatures[options[index].name]?.plus(1) ?: 1


        //Update the name to only show the selected options.
        var newNames = ""
        for(i in names.indices) {
            if(selectedList[i] > 0) {
                newNames += names[i] + " "
            }
        }
        selectedNames.value = (newNames)
    }

    override fun decrementSelection(index: Int) {
        selectedFeatures[options[index].name]=
            selectedFeatures[options[index].name]?.minus(1) ?: 0
    }

    override fun getMaxSameSelectionsAt(index: Int): Int {
        //TODO impl me
        return 1
    }

    fun getSelected() : List<Feature> {
        val result = mutableListOf<Feature>()
        selectedFeatures.forEach {
            for(x in 0 until it.value) {
                result.add(options.first { feature -> feature.name == it.key })
            }
        }
        return result
    }
}