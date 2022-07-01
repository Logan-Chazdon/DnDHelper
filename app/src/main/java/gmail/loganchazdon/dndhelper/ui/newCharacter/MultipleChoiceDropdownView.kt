package gmail.loganchazdon.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownState

@Composable
fun MultipleChoiceDropdownView(state : MultipleChoiceDropdownState) {
    var expanded by remember { mutableStateOf(false) }

    val text  = state.selectedNames.observeAsState()
    Text(
        text = "${text.value}",
        modifier = Modifier
            .clickable { expanded = true }
    )
    Column {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            state.names.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    if (state.selectedList[index] >= state.getMaxSameSelectionsAt(index)) {
                        state.decrementSelection(index)
                    } else {
                        state.incrementSelection(index)
                    }
                }) {
                    //If we can only select each item once make a checkbox
                    if (state.getMaxSameSelectionsAt(index) == 1) {
                        Checkbox(
                            checked = state.selectedList[index] != 0,
                            onCheckedChange = null
                        )
                    } else { //If we can select each item more than once.
                        Text(text = "+${state.selectedList[index]} ")
                    }
                    Text(text = item)
                    Spacer(modifier = Modifier.width(15.dp))

                    if (state.getMaxSameSelectionsAt(index) != 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
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
        state.subChoiceKeys?.let { keys ->
            Column(Modifier.padding(start = 8.dp)) {
                keys.forEach {
                    MultipleChoiceDropdownView(state = state.getSubChoiceAt(it)!!)
                }
            }
        }
    }
}