package ui.newCharacter.stateHolders

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import model.*
import ui.newCharacter.utils.getDropDownState


class MultipleChoiceDropdownStateFeatureImpl(
    val feature: Feature,
    val choiceIndex: Int
) : MultipleChoiceDropdownState {
    var character: Character? = null
    var assumedProficiencies: List<Proficiency> = listOf()
    var level: Int = 1
    var assumedClass: Class? = null
    var assumedSpells: List<Spell> = listOf()
    var assumedStatBonuses: Map<String, Int>? = null
    var assumedFeatures: List<Feature> = listOf()
    override val selectedNames: MutableStateFlow<String> = MutableStateFlow(feature.name)
    override val getDescription: ((Int) -> String)
        get() = { index ->
            options[index].description
        }
    override var choiceName = ""
        set(newName) {
            field = newName
            if(selectedNames.value == "")
                selectedNames.value = newName
        }
    private val selectedFeatures: SnapshotStateMap<String, Int> = mutableStateMapOf()

    init {
        feature.choices?.getOrNull(choiceIndex)?.chosen?.forEach {
            selectedFeatures[it.name] = (selectedFeatures[it.name] ?: 0) + 1
        }

        if(feature.choices?.getOrNull(choiceIndex)?.chosen != null) {
            //Update the name to only show the selected options.
            var newNames = ""
            selectedFeatures.filter { it.value != 0}.forEach {
                newNames += it.key + " "
            }
            if(newNames.isNotBlank()) {
                selectedNames.value = (newNames)
            }
        }
    }

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
        return feature.getAvailableOptionsAt(
            index = choiceIndex,
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
        get() = feature.choices?.get(choiceIndex)?.choose?.num(level) ?: 0

    override val subChoiceKeys: List<String>
        get() {
            val result = mutableListOf<String>()
            getSelectedWithoutSubFeatures().forEachIndexed { i, it ->
                if ((it.choices?.getOrNull(choiceIndex)?.choose?.num(level) ?: 0) != 0) {
                    result += getOverrideKey(it, i)
                }
            }
            return result
        }

    override var costs: List<Int>
        get() = listOf()
        set(value) {}

    private fun getOverrideKey(feature: Feature, index: Int) : String {
        return feature.name + index
    }


    private val subChoices: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl> = mutableStateMapOf()
    override fun getSubChoiceAt(key: String): MultipleChoiceDropdownState? {
       var subFeature : Feature? = null

       for((i, it) in getSelectedWithoutSubFeatures().withIndex()) {
           if(getOverrideKey(it, i) == key) {
               subFeature = it
               break
           }
       }

       return subFeature?.let {
           subChoices.getDropDownState(
               choiceIndex = choiceIndex,
               feature = it,
               character = character,
               assumedProficiencies = assumedProficiencies,
               assumedClass = assumedClass,
               assumedStatBonuses = assumedStatBonuses,
               assumedSpells = assumedSpells,
               assumedFeatures = assumedFeatures,
               level = level,
               overrideKey = key
           )
       }
    }


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
        if((selectedFeatures[options[index].name] ?: 1) > 0) {
            selectedFeatures[options[index].name] =
                selectedFeatures[options[index].name]?.minus(1) ?: 0
        }
    }

    override fun getMaxSameSelectionsAt(index: Int): Int {
        return options[index].maxTimesChosen ?: 1
    }

    private fun getSelectedWithoutSubFeatures() : List<Feature> {
        val result = mutableListOf<Feature>()
        selectedFeatures.forEach {
            for(x in 0 until it.value) {
                options
                    .firstOrNull { feature -> feature.name == it.key }?.copy()?.let { newFeature ->
                        result.add(newFeature)
                    }
            }
        }
        return result
    }

    fun getSelected() : List<Feature> {
        val result = getSelectedWithoutSubFeatures()
        result.forEachIndexed { index, it ->
            it.choices?.forEach { featureChoice ->
                if (featureChoice.choose.num(level) > 0) {
                    featureChoice.chosen = (getSubChoiceAt(
                        getOverrideKey(it, index)
                    ) as MultipleChoiceDropdownStateFeatureImpl).getSelected()
                }
            }
        }
        return result
    }
}
