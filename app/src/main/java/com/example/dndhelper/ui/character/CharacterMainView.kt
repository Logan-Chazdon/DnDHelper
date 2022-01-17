package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        
                        CharacterTextView(
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
                            name = "Ideals",
                            value = viewModel.character?.observeAsState()?.value?.ideals ?: "",
                            onChange = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.setIdeals(it)
                                }
                            }
                        )

                        CharacterTextView(
                            name = "Bonds",
                            value = viewModel.character?.observeAsState()?.value?.bonds ?: "",
                            onChange = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.setBonds(it)
                                }
                            }
                        )

                        CharacterTextView(
                            name = "Flaws",
                            value = viewModel.character?.observeAsState()?.value?.flaws ?: "",
                            onChange = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.setFlaws(it)
                                }
                            }
                        )
                    }
                    Column() {
                        FeaturesAndTraitsView(
                            features = viewModel.characterFeatures.observeAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.4f)
                        )

                        Spacer(Modifier.height(8.dp))

                        LanguagesAndProficienciesView(
                            languages = viewModel.character?.observeAsState()?.value?.languages ?: listOf(),
                            proficiencies = viewModel.character?.observeAsState()?.value?.proficiencies ?: listOf(),
                            modifier = Modifier.fillMaxHeight(0.4f)
                        )
                    }
                }
            }
        }
    }
}