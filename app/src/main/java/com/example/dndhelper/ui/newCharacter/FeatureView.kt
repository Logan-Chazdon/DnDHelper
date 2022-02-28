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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feature
import com.example.dndhelper.repository.dataClasses.Proficiency
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState

@Composable
fun FeatureView(
    feature: Feature,
    level : Int,
    proficiencies: List<Proficiency>,
    character: Character?,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownState>
) {
    val color = if (feature.choose.num(level) != 0) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.onBackground.copy(alpha = 0.3f)
            .compositeOver(MaterialTheme.colors.background)
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
                val options = feature.getAvailableOptions(
                    character,
                    proficiencies,
                    level
                )
                MultipleChoiceDropdownView(
                    state = dropDownStates.getDropDownState(
                        key = feature.name + feature.grantedAtLevel,
                        choiceName = feature.name,
                        maxSelections = feature.choose.num(level),
                        names = options.let { list ->
                            val result = mutableListOf<String>()
                            list.forEach {
                                result.add(it.name)
                            }
                            result
                        }
                    )
                )
            }
        }
    }

}