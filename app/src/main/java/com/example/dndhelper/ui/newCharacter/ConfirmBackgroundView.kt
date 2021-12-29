package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dndhelper.repository.dataClasses.LanguageChoice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

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
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(start = 10.dp)
                    .verticalScroll(scrollState)
            ) {
                Row() {
                    Text(text = background.name, fontSize = 24.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = {
                                GlobalScope.launch{
                                    viewModel.setBackGround(background)
                                    //Navigate to the next step
                                    Handler(mainLooper).post {
                                        navController.navigate("newCharacterView/StatsView/${viewModel.id}")
                                    }
                                }

                            }
                        ) {
                            Text(text = "Add background")
                        }

                    }
                }
                Text(text = background.desc, fontSize = 16.sp, modifier = Modifier.padding(start = 10.dp))

                background.equipmentChoices.forEach { choice ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        var selectedIndex by remember { mutableStateOf(0) }

                        Column()
                        {
                            Text(
                                text = choice.name,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp)
                            )

                            Text(
                                choice.from[selectedIndex].name,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = { expanded = true })
                                    .background(
                                        Color.White
                                    )
                                    .padding(start = 15.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            choice.from.forEachIndexed { index, item ->
                                DropdownMenuItem(onClick = {
                                    selectedIndex = index
                                    expanded = false
                                }) {
                                    Text(text = item.name)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                if(background.proficiencies.isNotEmpty() || background.toolProficiencies.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        var proficiencies = ""
                        background.proficiencies.forEach {
                            proficiencies += it.name + " "
                        }
                        background.toolProficiencies.forEach {
                            proficiencies += it.name + " "
                        }
                        Row(modifier = Modifier.padding(5.dp)) {
                            Text(text = "Proficiencies: $proficiencies")
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                if(background.languageChoices.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        background.languageChoices.forEach { choice ->
                            var expanded by remember { mutableStateOf(false) }
                            val from = viewModel.getLanguageChoice(choice)

                            Column {
                                Text(
                                    text = choice.name,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 5.dp)
                                )

                                viewModel.selectedNames.value = choice.name

                                //TODO fix this. It isnt updating on change.
                                val text = viewModel.selectedNames.observeAsState()
                                Text(
                                    text = text.value!!,
                                    modifier = Modifier
                                        .clickable { expanded = true }
                                        .padding(start = 15.dp)
                                )

                                //This ensures the list is long enough to avoid an exception
                                viewModel.setLanguageChoiceLength(from.size)

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    from.forEachIndexed { index, item ->
                                        DropdownMenuItem(onClick = {
                                            viewModel.changeSelection(index, choice.choose)
                                        }) {
                                            Checkbox(
                                                checked = viewModel.selectedList[index],
                                                onCheckedChange = null
                                            )
                                            Text(text = item.name.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Spacer(Modifier.height(10.dp))

                background.features.forEach {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text(text = it.name, fontSize = 16.sp)
                            Text(text = it.description, modifier = Modifier.padding(start = 5.dp))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

            }

        }
    }
}