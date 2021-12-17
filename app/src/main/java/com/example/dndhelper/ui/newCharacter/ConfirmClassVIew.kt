package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Exception

@Composable
fun ConfirmClassView(viewModel: NewCharacterViewModel,classIndex: Int) {
    val classes = viewModel.classes.observeAsState()
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
                    //TODO add the class levels
                },
            ) {
                Text(text = "Add class")
            }
        }

        val levels = remember {
            mutableStateOf(TextFieldValue("1"))
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
            val checked = remember{ mutableStateOf(true) } //TODO when we implement the classes make this rely on the viewModel
            Text(text = "Use as base class", fontSize = 20.sp)
            Checkbox(
                checked = checked.value,
                onCheckedChange = { checked.value = it }
            )
        }

        Spacer(modifier = Modifier.height(5.dp))


        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.LightGray)
                .verticalScroll(state = scrollState, enabled = true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                                .height(100.dp)
                                .background(color = color, shape = RoundedCornerShape(10.dp)),
                            backgroundColor = color
                        ) {
                            var expanded by remember { mutableStateOf(false) }
                            var selectedLastIndex by remember { mutableStateOf(0) }
                            var selectedFirstIndex by remember { mutableStateOf(0) }
                            Column() {
                                Text(text = choice.name, fontSize = 18.sp)

                                if (choice.choiceNum != 0) {
                                    Text(
                                        choice.options!![selectedLastIndex].name,
                                        modifier = Modifier.fillMaxWidth()
                                            .clickable(onClick = { expanded = true }).background(
                                            Color.White
                                        ).padding(start = 5.dp)
                                    )

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        choice.options.forEachIndexed { index, item ->
                                            DropdownMenuItem(onClick = {
                                                selectedFirstIndex = selectedLastIndex  //TODO add a pop up that shows the description and allows you to add or deny the feature
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