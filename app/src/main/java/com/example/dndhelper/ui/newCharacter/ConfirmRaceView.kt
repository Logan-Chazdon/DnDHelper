package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Feature
import com.example.dndhelper.repository.dataClasses.Proficiency
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun ConfirmRaceView(
    viewModel: NewCharacterRaceViewModel,
    navController: NavController,
    raceIndex: Int,
    characterId: Int
) {
    viewModel.raceIndex = raceIndex
    val races = viewModel.races.observeAsState()
    val scrollState = rememberScrollState(0)
    viewModel.id = characterId
    val mainLooper = Looper.getMainLooper()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
            ?.let { it.alignment?.let { it1 -> Text(text = it1, Modifier.padding(start = 5.dp, top = 5.dp)) } }
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
            RaceContentCard("Languages") {
                races.value?.get(raceIndex)?.languageDesc?.let { Text(text = it) }
                if(!races.value?.get(raceIndex)?.languageChoices.isNullOrEmpty()) {
                    //TODO
                }
            }

            races.value?.get(raceIndex)?.abilityBonuses?.let { bonuses ->
                RaceContentCard("Ability bonuses") {
                        Row {
                            bonuses.forEach {
                                Text(text = "${it.ability} +${it.bonus}  ")
                            }
                        }
                }
            }

            races.value?.get(raceIndex)?.let { race ->
                    RaceFeaturesView(
                        character = viewModel.character.observeAsState().value,
                        features = viewModel.filterRaceFeatures(race),
                        dropDownStates = viewModel.raceFeaturesDropdownStates,
                        proficiencies = viewModel.proficiencies
                    )
            }

            races.value?.get(raceIndex)?.subraces?.let { subraces ->
                if (races.value?.get(raceIndex)?.subraces?.isNotEmpty() == true) {
                    RaceContentCard("Subrace") {
                            subraces[viewModel.subraceIndex.value].let { subrace ->
                                Box {
                                    var expanded by remember { mutableStateOf(false) }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        modifier = Modifier.clickable { expanded = true }
                                    ) {
                                        Text(
                                            text = subrace.name,
                                            style = MaterialTheme.typography.body1,
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            "Drop down"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        races.value?.get(raceIndex)?.subraces!!.forEachIndexed { index, item ->
                                            DropdownMenuItem(onClick = {
                                                viewModel.subraceIndex.value = index
                                                expanded = false
                                            }) {
                                                Text(text = item.name)
                                            }

                                        }
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    subrace.size?.let {
                                        Text(it)
                                    }
                                    subrace.groundSpeed?.let {
                                        Text("${it}ft")
                                    }
                                }
                            }
                    }

                    subraces[viewModel.subraceIndex.value].abilityBonuses?.let { bonuses ->
                        RaceContentCard("Ability bonuses") {
                                Row {
                                    bonuses.forEach {
                                        Text(text = "${it.ability} +${it.bonus}  ")
                                    }
                                }
                            }
                    }

                    RaceFeaturesView(
                        character = viewModel.character.observeAsState().value,
                        features = subraces[viewModel.subraceIndex.value].traits,
                        dropDownStates = viewModel.subraceFeaturesDropdownStates,
                        proficiencies = viewModel.proficiencies
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceContentCard(
    title: String,
    Content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.95f),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 5.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(5.dp)) {
            Text(title, style = MaterialTheme.typography.h6)
            Content()
        }
    }
}

@Composable
private fun RaceFeaturesView(
    character: Character?,
    features : List<Feature>,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownState>,
    proficiencies : List<Proficiency>
) {
    features.forEach { feature ->
        FeatureView(
            feature = feature,
            level = character?.totalClassLevels ?: 0,
            proficiencies = proficiencies,
            character = character,
            dropDownStates = dropDownStates
        )
    }
}

