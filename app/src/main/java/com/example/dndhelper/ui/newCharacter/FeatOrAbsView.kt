package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import java.lang.IndexOutOfBoundsException
import kotlin.math.exp

@Composable
fun FeatOrAbsView(viewModel: FeatOrAbsViewModel, navController: NavController) {
    Scaffold(
        floatingActionButton = {
            Button(onClick = {
                navController.navigate("characterView/MainView/${viewModel.id}")
            }) {
                Text("Finish!")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (it in 0..(viewModel.featOrAbsNum.value ?: 0)) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    elevation = 10.dp
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Column {
                        Text(
                            text = "Feat or Ability score increase",
                            modifier = Modifier.clickable { expanded = !expanded })

                        //Fill out the list
                        try {
                            viewModel.isFeat[it]
                        } catch (e: IndexOutOfBoundsException) {
                            viewModel.isFeat.add(it, false)
                        }

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = { viewModel.isFeat[it] = true }) {
                                Text(text = "Feat")
                            }
                            DropdownMenuItem(onClick = { viewModel.isFeat[it] = false }) {
                                Text(text = "Ability Score Increase")
                            }
                        }


                        if (viewModel.isFeat[it]) {
                            viewModel.featNames.observeAsState().value?.let { featNames ->
                                viewModel.featDropDownStates
                                    .getDropDownState(
                                        key = it,
                                        maxSelections = 1,
                                        names = featNames,
                                        choiceName = "Feat"
                                    )
                            }?.let { state ->
                                MultipleChoiceDropdownView(
                                    state = state
                                )
                            }
                        } else {
                            MultipleChoiceDropdownView(
                                state = viewModel.absDropDownStates
                                    .getDropDownState(
                                        key = it,
                                        maxSelections = 2,
                                        names = viewModel.abilityNames,
                                        choiceName = "Ability Score Improvement",
                                        maxOfSameSelection = 2
                                    )
                            )
                        }

                    }
                }
                Spacer(Modifier.padding(top = 8.dp))
            }
        }
    }
}