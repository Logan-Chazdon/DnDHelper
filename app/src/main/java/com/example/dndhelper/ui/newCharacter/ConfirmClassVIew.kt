package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dndhelper.repository.dataClasses.Proficiency
import java.lang.Exception
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ConfirmClassView(viewModel: NewCharacterClassViewModel, navController: NavController, classIndex: Int, characterId: Int) {
    val classes = viewModel.classes.observeAsState()
    viewModel.id = characterId
    val mainLooper = Looper.getMainLooper()

    val levels = remember {
        mutableStateOf(TextFieldValue("1"))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            //Text
            classes.value?.get(classIndex)?.let {
                Text(
                    text = it.name,
                    fontSize = 32.sp,
                )
            }

            //Add Class Button
            Button(
                onClick = {
                    GlobalScope.launch{
                        classes.value?.get(classIndex)?.let { viewModel.addClassLevels(it, levels.value.text.toInt()) }
                        //Navigate to the next step
                        Handler(mainLooper).post {
                            navController.navigate("newCharacterView/RaceView/${viewModel.id}")
                        }
                    }
                },
            ) {
                Text(text = "Add class")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Text(text = "Level: ", fontSize = 24.sp)

            TextField(
                value = levels.value,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = {
                    try {
                       if(it.text.toInt() in 1..20)
                           levels.value = it

                    } catch (e : Exception) {
                        if(it.text.isEmpty())
                            levels.value = it
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Use as base class", fontSize = 20.sp)
            Checkbox(
                checked = viewModel.isBaseClass.value,
                onCheckedChange = { viewModel.isBaseClass.value = it }
            )
        }

        Spacer(modifier = Modifier.height(5.dp))


        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(state = scrollState, enabled = true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(viewModel.isBaseClass.value) {
                val proficiencyChoices = viewModel.classes.observeAsState().value?.get(classIndex)?.proficiencyChoices
                proficiencyChoices?.forEach { choice ->
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                        backgroundColor = Color.White
                    ) {
                        Column() {
                            Text(text = choice.name, fontSize = 18.sp)



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
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val equipmentChoices = viewModel.classes.observeAsState().value?.get(classIndex)?.equipmentChoices
                equipmentChoices?.forEach { choice ->
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                        backgroundColor = Color.White
                    ) {
                        Column() {
                            Text(text = choice.name, fontSize = 18.sp)

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
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }







            val levelPath = viewModel.classes.observeAsState().value?.get(classIndex)?.levelPath
            if (levelPath != null) {
                for(choice in levelPath) {
                    if(levels.value.text.isNotBlank())
                        if (choice.level <= levels.value.text.toInt()) {
                        val color = if (choice.choiceNum != 0) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                        Card(
                            elevation = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .background(color = color, shape = RoundedCornerShape(10.dp)),
                            backgroundColor = color
                        ) {
                            var expanded by remember { mutableStateOf(false) }
                            var selectedLastIndex by remember { mutableStateOf(0) } //TODO change how we do this to for a more scaleable solution. maybe a for loop i < choose
                            var selectedFirstIndex by remember { mutableStateOf(0) }
                            Column() {
                                Text(text = choice.name, fontSize = 18.sp)
                                Text(text = choice.description, fontSize = 12.sp, modifier = Modifier.padding(start = 5.dp))


                                if (choice.choiceNum != 0) {
                                    Text(
                                        choice.options!![selectedLastIndex].name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = { expanded = true })
                                            .background(
                                                Color.White
                                            )
                                            .padding(start = 5.dp)
                                    )

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        choice.options.forEachIndexed { index, item ->
                                            DropdownMenuItem(onClick = {
                                                selectedFirstIndex = selectedLastIndex
                                                selectedLastIndex = index
                                                expanded = false
                                            }) {
                                                Text(text = item.name)
                                            }

                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }
            }
        }
    }
}

