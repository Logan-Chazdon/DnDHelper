package ui.newCharacter.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import model.Feat
import model.Feature
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl

fun getFeatsAt(
    i: Int,
    level: Int,
    featDropDownStates: SnapshotStateList<MultipleChoiceDropdownStateImpl>,
    featChoiceDropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateImpl>,
    feats: List<Feat>
    ): List<Feat> {
    return try {
        (featDropDownStates[i].getSelected(feats) as List<Feat>).run {
            this.forEach { feat ->
                feat.features?.forEach { feature ->
                    feature.choices?.forEach { featureChoice ->
                        if (featureChoice.choose.num(level) != 0) {
                            featureChoice.chosen = featureChoice.options?.let {
                                (
                                        featChoiceDropDownStates.getDropDownState(
                                            key = "${feature.name}$i",
                                            maxSelections = featureChoice.choose.num(level),
                                            names = featureChoice.options.let { featureList ->
                                                val result = mutableListOf<String>()
                                                featureList?.forEach {
                                                    result.add(it.name)
                                                }
                                                result
                                            },
                                            choiceName = feature.name,
                                            maxOfSameSelection = 1
                                        )
                                        ).getSelected(it)
                            } as List<Feature>
                        }
                    }
                }
            }
            this
        }
    } catch (e: IndexOutOfBoundsException) {
        listOf()
    }
}