package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.dndhelper.ui.SpellDetailsView
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import com.example.dndhelper.ui.theme.noActionNeeded
import com.example.dndhelper.ui.utils.allNames
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
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
                        backgroundColor = MaterialTheme.colors.noActionNeeded,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(start = 5.dp)) {
                            Text(
                                text = "Equipment",
                                style = MaterialTheme.typography.h6
                            )
                            Text(background.equipment.let { items ->
                                var result = ""
                                items.forEachIndexed { i, item ->
                                    result += item.name
                                    if (i != items.size - 1) {
                                        result += ", "
                                    }
                                }
                                result.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                            })
                        }
                    }

                    background.equipmentChoices.forEach { choice ->
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.surface,
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
                                    item.allNames.let { names.add(it) }
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

                    if (background.proficiencies.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.noActionNeeded,
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
                                Text(text = proficiencies)
                            }
                        }
                    }

                    if (background.languageChoices.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.surface,
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


                    background.spells?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.noActionNeeded,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(
                                    text = "Spells",
                                    style = MaterialTheme.typography.h6
                                )
                                it.forEach {
                                    var expanded by remember { mutableStateOf(false) }
                                    Text(
                                        text = it.name,
                                        modifier = Modifier.clickable {
                                            expanded = true
                                        }
                                    )
                                    if(expanded) {
                                        Dialog(
                                            onDismissRequest = {
                                                expanded = false
                                            },
                                            properties = DialogProperties(
                                                usePlatformDefaultWidth = false,
                                                dismissOnClickOutside = true
                                            )
                                        ) {
                                            Card {
                                               SpellDetailsView(spell = it)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    background.features.forEach {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = if(it.choose.num(1) != 0) MaterialTheme.colors.surface else MaterialTheme.colors.noActionNeeded,
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