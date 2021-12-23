package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
    if (background != null) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.95f).padding(start = 10.dp).verticalScroll(scrollState)
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
                                modifier = Modifier.fillMaxWidth()
                                    .clickable(onClick = { expanded = true }).background(
                                        Color.White
                                    ).padding(start = 15.dp)
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

                if(background.proficiencies.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        Row(modifier = Modifier.padding(5.dp)) {
                            Text(text = "Proficiencies: ")

                            background.proficiencies.forEach {
                                Text(text = it.name + " ")
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