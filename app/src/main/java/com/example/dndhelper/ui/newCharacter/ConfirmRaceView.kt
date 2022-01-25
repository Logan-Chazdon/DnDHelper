package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun ConfirmRaceView(viewModel: NewCharacterRaceViewModel, navController: NavController, raceIndex: Int, characterId: Int) {
    val races = viewModel.races.observeAsState()
    val scrollState = rememberScrollState(0)
    viewModel.id = characterId
    val mainLooper = Looper.getMainLooper()
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            races.value?.get(raceIndex)?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.h4,
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = it.size,
                    fontSize = 16.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        //Change the race
                        GlobalScope.launch {
                            viewModel.setRace(it)
                            //Navigate to the next step
                            Handler(mainLooper).post {
                                navController.navigate("newCharacterView/BackgroundView/${viewModel.id}")
                            }
                        }

                    }) {
                        Text(text = "Set as race")
                    }
                }
            }
        }

        races.value?.get(raceIndex)
            ?.let { Text(text = it.sizeDesc, Modifier.padding(start = 5.dp, top = 5.dp)) }
        races.value?.get(raceIndex)
            ?.let { Text(text = it.alignment, Modifier.padding(start = 5.dp, top = 5.dp)) }
        races.value?.get(raceIndex)?.let {
            Text(
                text = "Speed: ${it.groundSpeed}",
                Modifier.padding(start = 5.dp, top = 5.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(5.dp)
                ) {
                    if (races.value?.get(raceIndex)?.subraces?.isNotEmpty() == true) {
                        var subraceIndex = remember {
                            0
                        }
                        var expanded by remember { mutableStateOf(false) }
                        val subrace = races.value!![raceIndex].subraces[subraceIndex]
                        Text(
                            text = subrace.name,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { expanded = true })
                        Spacer(Modifier.height(2.dp))
                        Text(text = subrace.desc, modifier = Modifier.padding(start = 10.dp))


                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            races.value?.get(raceIndex)?.subraces!!.forEachIndexed { index, item ->
                                DropdownMenuItem(onClick = {
                                    subraceIndex = index
                                    expanded = false
                                }) {
                                    Text(text = item.name)
                                }

                            }
                        }

                        subrace.racialTraits.forEach {
                            Text(text = it.name, style = MaterialTheme.typography.h6)
                            Spacer(Modifier.height(2.dp))
                            Text(text = it.description)
                        }
                    }

                    races.value?.get(raceIndex)?.traits?.forEach { trait ->
                        Text(text = trait.name, style = MaterialTheme.typography.h6)
                        Text(text = trait.description)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(Modifier.padding(5.dp)) {
                    Text("Languages", style = MaterialTheme.typography.h6)
                    races.value?.get(raceIndex)?.languageDesc?.let { Text(text = it) }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(Modifier.padding(5.dp)) {
                    Text("Languages", style = MaterialTheme.typography.h6)
                    Row {
                        races.value?.get(raceIndex)?.abilityBonuses?.forEach {
                            Text(text = "${it.ability} +${it.bonus}  ")
                        }
                    }
                }
            }
        }
    }
}

