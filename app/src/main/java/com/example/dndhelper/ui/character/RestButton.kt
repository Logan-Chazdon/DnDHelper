package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RestButton(viewModel: CharacterMainViewModel?) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRestType by remember { mutableStateOf(0) }
    Button(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Rest", modifier = Modifier.padding(top = 11.dp, bottom = 11.dp))
    }

    if(expanded) {
        Dialog(onDismissRequest = { expanded = false }) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .fillMaxWidth(0.9f)
            ) {
                Column{
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxHeight(0.8f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRestType == 0,
                                onClick = { selectedRestType = 0 })
                            Text("Long Rest")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRestType == 1,
                                onClick = { selectedRestType = 1 })
                            Text("Short Rest")
                        }


                        when(selectedRestType) {
                            //Long rest
                            0 -> {
                                Text(
                                    text = "A Long Rest is a period of extended downtime, at least 8 hours long, during which a character sleeps or performs light activity: reading, talking, eating, or standing watch for no more than 2 hours. If the rest is interrupted by a period of strenuous activity—at least 1 hour of walking, Fighting, casting Spells, or similar Adventuring activity—the Characters must begin the rest again to gain any benefit from it." ,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            //Short rest
                            1 -> {
                                Text(
                                    text = "A Short Rest is a period of downtime, at least 1 hour long, during which a character does nothing more strenuous than eating, drinking, reading, and tending to wounds.\n" +
                                            "\n" +
                                            "A character can spend one or more Hit Dice at the end of a Short Rest, up to the character’s maximum number of Hit Dice, which is equal to the character’s level. For each Hit Die spent in this way, the player rolls the die and adds the character’s Constitution modifier to it. The character regains Hit Points equal to the total. The player can decide to spend an additional Hit Die after each roll. A character regains some spent Hit Dice upon finishing a Long Rest, as explained below." ,
                                    modifier = Modifier.padding(8.dp)
                                )
                                //TODO implement hit dice.
                            }
                        }

                    }
                    val scope = rememberCoroutineScope { Dispatchers.IO }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = { expanded = false }) {
                            Text("Cancel")
                        }

                        Button(onClick = {
                            when(selectedRestType) {
                                //Long rest
                                0 -> {
                                    scope.launch {
                                        viewModel?.longRest()
                                    }
                                }
                                //Short rest
                                1 -> {
                                    scope.launch {
                                        viewModel?.shortRest() //TODO pass in hit die data
                                    }
                                }
                            }
                            expanded = false
                        }) {
                            Text("Rest")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRestButton() {
    RestButton(viewModel = null)
}