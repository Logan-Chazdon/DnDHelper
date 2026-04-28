package ui.newCharacter.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import model.Feat
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl

fun getFeatsAt(
    i: Int,
    level: Int,
    featDropDownStates: SnapshotStateList<MultipleChoiceDropdownStateImpl>,
    featChoiceDropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>,
    feats: List<Feat>
): List<Feat> {
    return try {
        featDropDownStates[i].getSelected(feats).run {
            this.forEach { feat ->
                feat.features?.forEach { feature ->
                    feature.choices?.forEachIndexed { index, featureChoice ->
                        if (featureChoice.choose.num(level) != 0) {
                            featureChoice.chosen = featureChoice.options?.let {
                                featChoiceDropDownStates.getDropDownState(
                                    choiceIndex = index,
                                    feature = feature,
                                    character = null,
                                    assumedProficiencies = emptyList(),
                                    level = level,
                                    assumedClass = null,
                                    assumedSpells = emptyList(),
                                    assumedStatBonuses = emptyMap(),
                                    assumedFeatures = emptyList(),
                                    overrideKey = null
                                ).getSelected()
                            }
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