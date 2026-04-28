package ui.newCharacter

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import model.Feat
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import ui.newCharacter.utils.getDropDownState

@Composable
fun FeatView(
    level: Int,
    key: Int,
    featNames: List<String>,
    feats: List<Feat>,
    featDropDownStates: SnapshotStateList<MultipleChoiceDropdownStateImpl>,
    featChoiceDropDownState: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>,
) {
    val state = featDropDownStates.getDropDownState(
        key = key,
        maxSelections = 1,
        names = featNames,
        choiceName = "Feat"
    )
    Column {
        MultipleChoiceDropdownView(
            state = state
        )


        state.getSelected(feats).getOrNull(0)?.let { feat ->
            Text(feat.desc)

            feat.features?.forEach { feature ->
                feature.choices?.filter { it.choose.num(level) != 0 }?.forEachIndexed { index, _ ->
                    MultipleChoiceDropdownView(
                        state = featChoiceDropDownState.getDropDownState(
                            choiceIndex = index,
                            feature = feature,
                            character = null,
                            assumedProficiencies = emptyList(),
                            level = 1,
                            assumedClass = null,
                            assumedSpells = emptyList(),
                            assumedStatBonuses = emptyMap(),
                            assumedFeatures = emptyList(),
                            overrideKey = null
                        )
                    )
                }
            }
        }
    }
}