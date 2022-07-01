package gmail.loganchazdon.dndhelper.ui.newCharacter.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl

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
                    if (feature.choose.num(level) != 0) {
                        feature.chosen = feature.options?.let {
                            (
                                    featChoiceDropDownStates.getDropDownState(
                                        key = "${feature.name}$i",
                                        maxSelections = feature.choose.num(level),
                                        names = feature.options.let { featureList ->
                                            val result = mutableListOf<String>()
                                            featureList.forEach {
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
            this
        }
    } catch (e: IndexOutOfBoundsException) {
        listOf()
    }
}