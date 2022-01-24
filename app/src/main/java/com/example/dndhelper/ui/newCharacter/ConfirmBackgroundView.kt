package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ConfirmBackgroundView(
    characterId: Int,
    viewModel: NewCharacterBackgroundViewModel,
    navController: NavHostController,
    backgroundIndex: Int
) {
    val backgrounds = viewModel.backgrounds.observeAsState()
    val background = backgrounds.value?.get(backgroundIndex)
    val scrollState = rememberScrollState(0)
    val mainLooper = Looper.getMainLooper()
    viewModel.id = characterId
    viewModel.backgroundIndex = backgroundIndex
    if (background != null) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                    .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = background.name, style = MaterialTheme.typography.h4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                GlobalScope.launch {
                                    viewModel.setBackGround(background)
                                    //Navigate to the next step
                                    Handler(mainLooper).post {
                                        navController.navigate("newCharacterView/StatsView/${viewModel.id}")
                                    }
                                }

                            }
                        ) {
                            Text(text = "Set")
                        }

                    }
                }
                Text(
                    text = background.desc,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(start = 5.dp)) {
                            Text(
                                text = "Equipment",
                                style = MaterialTheme.typography.h6
                            )
                            Text(background.equipment.let {
                                var result = ""
                                it.forEachIndexed { i, item ->
                                    result += item.name
                                    if (i != it.size - 1) {
                                        result += ", "
                                    }
                                }
                                result
                            })
                        }
                    }

                    background.equipmentChoices.forEach { choice ->
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = Color.White,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 5.dp)
                            ) {
                                Text(
                                    text = choice.name,
                                    style = MaterialTheme.typography.h6
                                )

                                //Tell the state bundle what the user can choose from.
                                val names = mutableListOf<String>()
                                for (item in choice.from) {
                                    item.name?.let { names.add(it) }
                                }

                                val multipleChoiceState = viewModel.dropDownStates.getDropDownState(
                                    key = choice.name,
                                    maxSelections = choice.choose,
                                    names = names,
                                    choiceName = choice.name
                                )

                                //Create the view.
                                MultipleChoiceDropdownView(state = multipleChoiceState)
                            }
                        }
                    }

                    if (background.proficiencies.isNotEmpty() || background.toolProficiencies.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = Color.White,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(
                                    text = "Proficiencies",
                                    style = MaterialTheme.typography.h6
                                )
                                var proficiencies = ""
                                background.proficiencies.forEach {
                                    proficiencies += it.name + " "
                                }
                                background.toolProficiencies.forEach {
                                    proficiencies += it.name + " "
                                }
                                Text(text = proficiencies)
                            }
                        }
                    }

                    if (background.languageChoices.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = Color.White,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            background.languageChoices.forEach { choice ->
                                Column(
                                    modifier = Modifier.padding(start = 5.dp)
                                ) {
                                    Text(
                                        text = choice.name,
                                        style = MaterialTheme.typography.h6,
                                    )

                                    //Tell the state bundle what the user can choose from.
                                    val names = mutableListOf<String>()
                                    for (item in choice.from) {
                                        item.name?.let { names.add(it) }
                                    }

                                    val multipleChoiceState =
                                        viewModel.dropDownStates.getDropDownState(
                                            key = choice.name,
                                            maxSelections = choice.choose,
                                            names = names,
                                            choiceName = choice.name
                                        )

                                    //Create the view.
                                    MultipleChoiceDropdownView(state = multipleChoiceState)
                                }
                            }
                        }
                    }


                    background.features.forEach {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = Color.White,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(text = it.name, style = MaterialTheme.typography.h6)
                                Text(text = it.description)
                            }
                        }
                    }
                }
            }

        }
    }
}