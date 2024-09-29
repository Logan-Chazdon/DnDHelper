package ui.newCharacter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.*
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import ui.newCharacter.utils.getDropDownState
import ui.theme.noActionNeeded

@Composable
fun FeatureView(
    feature: Feature,
    level : Int,
    proficiencies: List<Proficiency>,
    character: Character?,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>,
    assumedClass: Class?,
    assumedSpells: List<Spell>,
    assumedFeatures: List<Feature>,
    assumedStatBonuses: Map<String, Int>?
    ) {
    val color = if (feature.choices?.any { it.choose.num(level) != 0 } == true) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.noActionNeeded
    }
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(color = color, shape = RoundedCornerShape(10.dp)),
        backgroundColor = color
    ) {
        Column(Modifier.padding(start = 5.dp)) {
            Text(text = feature.name, style = MaterialTheme.typography.h6)
            Text(
                text = feature.description,
                style = MaterialTheme.typography.caption
            )

            feature.choices?.forEachIndexed { index, it ->
                if (it.choose.num(level) != 0 && it.options?.isNotEmpty() == true) {
                    MultipleChoiceDropdownView(
                        state = dropDownStates.getDropDownState(
                            choiceIndex = index,
                            feature = feature,
                            character = character,
                            assumedFeatures = assumedFeatures,
                            assumedProficiencies = proficiencies,
                            level = level,
                            assumedClass = assumedClass,
                            assumedStatBonuses = assumedStatBonuses,
                            assumedSpells = assumedSpells
                        )
                    )
                }
            }
        }
    }

}