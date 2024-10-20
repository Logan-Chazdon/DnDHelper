package gmail.loganchazdon.dndhelper.ui.newCharacter

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.utils.getDropDownState

@Composable
fun FeatView(
    level : Int,
    key : Int,
    featNames: MutableList<String>,
    feats: List<Feat>,
    featDropDownStates: SnapshotStateList<MultipleChoiceDropdownStateImpl>,
    featChoiceDropDownState: SnapshotStateMap<String, MultipleChoiceDropdownStateImpl>,
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
        (state.getSelected(feats) as List<Feat>).getOrNull(0)?.let { feat ->
            Text(feat.desc)
            feat.features?.forEach { feature ->
                feature.choices?.filter { it.choose.num(level) != 0 }?.forEach { featureChoice ->
                    MultipleChoiceDropdownView(
                        state = featChoiceDropDownState.getDropDownState(
                            key = "${feature.name}$key",
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
                    )
                }
            }
        }
    }
}