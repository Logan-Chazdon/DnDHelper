package ui.homebrew

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ui.newCharacter.AutoSave

@Composable
fun HomebrewRaceView(
    navController: NavController?,
    viewModel: HomebrewRaceViewModel
) {
    val scope = rememberCoroutineScope()
    val race = viewModel.race.collectAsState()

    navController?.let {
        AutoSave(
            "homebrewRaceView",
            { id ->
                viewModel.saveRace()
                id.value = viewModel.id
            },
            it,
            true
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                var id = 0
                scope.launch(/*Dispatchers.IO*/) {
                    id = viewModel.createDefaultFeature()
                }.invokeOnCompletion {
                    navController?.navigate("homebrewView/homebrewFeature/$id")
                }
            }) {
                Icon(Icons.Default.Add, "New Feature")
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth(0.95f)
                    .padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Name
                item {
                    OutlinedTextField(
                        value = viewModel.name.value,
                        onValueChange = { viewModel.name.value = it },
                        placeholder = {
                            Text(text = "Homebrew race name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h5
                    )
                }

                //Speed and size
                item {
                    SpeedAndSizeView(
                        speed = viewModel.speed,
                        sizeClass = viewModel.sizeClass,
                        sizeClassOptions = viewModel.sizeClassOptions
                    )
                }

                //Ability bonuses and feats
                item {
                    AbilityBonusesAndFeatsView(
                        viewModel.abilityBonuses,
                        viewModel.abilityBonusChoice
                    )
                }

                //Features
                race.value?.traits?.let {
                    if (it.isNotEmpty()) {
                        item {
                            FeaturesView(
                                features = it,
                                onDelete = { id ->
                                    viewModel.removeFeature(id)
                                },
                                onClick = { id ->
                                    navController?.navigate("homebrewView/homebrewFeature/$id")
                                }
                            )
                        }
                    }
                }


                //Subraces
                item {
                    GenericSelectionView(
                        onClick = {
                           var id = 0
                           scope.async {
                               id =  viewModel.subraces!!.last()[it].id
                            }.invokeOnCompletion {
                               navController!!.navigate("homebrewView/homebrewSubraceView/${id}")
                           }
                        },
                        chosen = viewModel.subraces?.collectAsState(emptyList())?.value.let {
                            val result = mutableListOf<String>()
                            it?.forEach {
                                result.add(it.name)
                            }
                            result
                        },
                        onDelete =  {
                            scope.launch {
                                viewModel.deleteSubraceAt(it)
                            }
                        },
                        onExpanded = {
                            var id =0
                            scope.launch {
                                id = viewModel.createDefaultSubrace()
                            }.invokeOnCompletion {
                                navController?.navigate("homebrewView/homebrewSubraceView/$id")
                            }
                        }
                    )
                }

            }
        }
    }
}