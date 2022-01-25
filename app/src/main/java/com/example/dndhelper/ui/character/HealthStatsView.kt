package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeathStatsView(
    currentHp: Int?,
    maxHp: Int?,
    tempHp: Int?,
    heal: () -> Unit,
    addTemp: () -> Unit,
    damage: () -> Unit
) {
    val titles = mapOf(
        "HP" to currentHp,
        "Temp HP" to tempHp,
        "Max HP" to maxHp
    )

    val buttons = listOf(
        "Heal" to heal,
        "Add Temp" to addTemp,
        "Damage" to damage
    )

    Card (
        modifier = Modifier.fillMaxWidth(0.95f).padding(5.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var i = 0
            for (item in titles) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier.size(85.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.key,
                                style = MaterialTheme.typography.subtitle1
                            )

                            Text(
                                text = item.value.toString(),
                                modifier = Modifier.padding(5.dp),
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = buttons[i].second
                    ) {
                        Text(buttons[i].first)
                    }
                }
                i += 1
            }
        }
    }
}