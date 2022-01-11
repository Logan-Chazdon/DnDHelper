package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndhelper.ui.utils.mediaQuery

@Composable
fun MultipleChoiceDropdownView(state : MultipleChoiceDropdownState) {
    var expanded by remember { mutableStateOf(false) }

    val text  = state.selectedNames.observeAsState()
    Text(
        text = "${text.value}",
        modifier = Modifier
            .clickable { expanded = true }
            .padding(start = 15.dp)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        state.names.forEachIndexed { index, item ->
            DropdownMenuItem(onClick = {
                state.incrementSelection(index)
            }) {
                    //If we can only select each item once make a checkbox
                    if (state.maxSameSelections == 1) {
                        Checkbox(
                            checked = state.selectedList[index] != 0,
                            onCheckedChange = null
                        )
                    } else { //If we can select each item more than once.
                        Text(text = "+${state.selectedList[index]} ")
                    }
                    Text(text = item)
                    Spacer(modifier = Modifier.width(15.dp))

                    if (state.maxSameSelections != 1) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(onClick = {
                                state.decrementSelection(index)
                            }) {
                                Text(text = "-", fontSize = 14.sp)
                            }
                        }
                    }
            }
        }
    }
}