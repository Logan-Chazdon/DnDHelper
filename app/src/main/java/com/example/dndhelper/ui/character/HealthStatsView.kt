package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){
        var i = 0
        for(item in titles) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    elevation = 5.dp,
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.key,
                            fontSize = 14.sp
                        )

                        Text(
                            text = item.value.toString(),
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp
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

            when(item.key) {
                "HP" -> { Text("+" )}
                "Temp HP" -> { Text("/" ) }
            }

            i += 1
        }
    }
}