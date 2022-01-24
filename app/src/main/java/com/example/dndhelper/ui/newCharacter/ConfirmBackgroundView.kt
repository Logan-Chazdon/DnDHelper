package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
                        Column(
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Text(
                                text = choice.name,
                                fontSize = 16.sp,
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
                            Column {
                                Text(
                                    text = choice.name,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 5.dp)
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