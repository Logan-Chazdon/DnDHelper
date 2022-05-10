package com.example.dndhelper.ui.newCharacter

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
import com.example.dndhelper.model.*
import com.example.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import com.example.dndhelper.ui.theme.noActionNeeded

@Composable
fun FeatureView(
    feature: Feature,
    level : Int,
    proficiencies: List<Proficiency>,
    character: Character?,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>,
    assumedClass: Class?,
    assumedSpells: List<Spell>,
    assumedStatBonuses: Map<String, Int>?
    ) {
    val color = if (feature.choose.num(level) != 0) {
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

            if (feature.choose.num(level) != 0) {
                MultipleChoiceDropdownView(
                    state = dropDownStates.getDropDownState(
                        feature = feature,
                        character = character,
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