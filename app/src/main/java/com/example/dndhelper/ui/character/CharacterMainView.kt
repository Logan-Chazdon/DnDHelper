package com.example.dndhelper.ui.character

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun CharacterMainView( viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.fillMaxSize(0.97f)
        ) {
            Column() {
                Row() {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        TextField(
                            value = viewModel.character?.observeAsState()?.value?.name ?: "",
                            onValueChange = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.setName(it)
                                }
                            },
                        )
                        var gridNotRow by remember { mutableStateOf(false) }

                        if (gridNotRow) {
                            val isVertical =
                                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
                            val topModifier = if (isVertical) {
                                Modifier
                            } else {
                                Modifier.fillMaxWidth(0.5f)
                            }
                            VariableOrientationView(
                                isVertical = isVertical,
                                verticalAlignment = Alignment.Top,
                                arrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Column {
                                    CharacterTextView(
                                        modifier = topModifier,
                                        name = "Personality Traits",
                                        value = viewModel.character?.observeAsState()?.value?.personalityTraits
                                            ?: "",
                                        onChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setPersonalityTraits(it)
                                            }
                                        }
                                    )

                                    CharacterTextView(
                                        modifier = topModifier,
                                        name = "Ideals",
                                        value = viewModel.character?.observeAsState()?.value?.ideals
                                            ?: "",
                                        onChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setIdeals(it)
                                            }
                                        }
                                    )
                                }

                                if (!isVertical)
                                    Spacer(Modifier.width(5.dp))

                                Column {
                                    CharacterTextView(
                                        modifier = Modifier.fillMaxWidth(),
                                        name = "Bonds",
                                        value = viewModel.character?.observeAsState()?.value?.bonds
                                            ?: "",
                                        onChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setBonds(it)
                                            }
                                        }
                                    )

                                    CharacterTextView(
                                        modifier = Modifier.fillMaxWidth(),
                                        name = "Flaws",
                                        value = viewModel.character?.observeAsState()?.value?.flaws
                                            ?: "",
                                        onChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setFlaws(it)
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            //Row


                        }



                    }


                    Column() {
                        FeaturesAndTraitsView(
                            features = viewModel.characterFeatures.observeAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.5f)
                        )

                        Spacer(Modifier.height(8.dp))

                        LanguagesAndProficienciesView(
                            languages = viewModel.character?.observeAsState()?.value?.languages ?: listOf(),
                            proficiencies = viewModel.character?.observeAsState()?.value?.proficiencies ?: listOf(),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}