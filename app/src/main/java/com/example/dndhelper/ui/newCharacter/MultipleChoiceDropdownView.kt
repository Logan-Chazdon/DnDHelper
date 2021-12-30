package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultipleChoiceDropdownView(state : MultipleChoiceDropdownState) {
    var expanded by remember { mutableStateOf(false) }

    //TODO fix this. It isn't updating on change.
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
                state.changeSelection(index)
            }) {
                Checkbox(
                    checked = state.selectedList[index],
                    onCheckedChange = null
                )
                Text(text = item)
            }
        }
    }
}