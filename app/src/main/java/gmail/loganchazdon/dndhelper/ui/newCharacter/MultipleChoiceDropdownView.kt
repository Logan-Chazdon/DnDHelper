package gmail.loganchazdon.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownState

@Composable
fun MultipleChoiceDropdownView(state: MultipleChoiceDropdownState) {
    var expanded by remember { mutableStateOf(false) }
    var detailsExpanded by remember { mutableStateOf(false) }
    val text = state.selectedNames.observeAsState()
    val lastSelected = remember {
        mutableStateOf(-1)
    }
    Column {
        Text(
            text = "${text.value}",
            modifier = Modifier
                .clickable { expanded = true }
        )

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
            ) {
                Card(
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 8.dp,
                    modifier = Modifier
                        .sizeIn(
                            minWidth = 112.dp,
                            maxWidth = 280.dp,
                            minHeight = 48.dp
                        )
                        .padding(vertical = 8.dp)
                        .width(IntrinsicSize.Min)
                ) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .width(IntrinsicSize.Max)
                    ) {
                        state.names.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                onClick = {
                                    if (state.selectedList[index] * (state.costs.getOrNull(index)
                                            ?: 1) >= state.getMaxSameSelectionsAt(index)
                                    ) {
                                        state.decrementSelection(index)
                                    } else {
                                        state.incrementSelection(index)
                                    }
                                    lastSelected.value = index
                                },
                            ) {
                                //If we can only select each item once make a checkbox
                                if (state.getMaxSameSelectionsAt(index) == 1 || state.costs.getOrNull(
                                        index
                                    ) == state.maxSelections
                                ) {
                                    Checkbox(
                                        checked = state.selectedList[index] != 0,
                                        onCheckedChange = null
                                    )
                                } else { //If we can select each item more than once.
                                    Text(text = "+${state.selectedList[index]} ")
                                }
                                Text(text = item)
                                Spacer(modifier = Modifier.width(15.dp))

                                if (state.getMaxSameSelectionsAt(index) != 1 && state.costs.getOrNull(
                                        index
                                    ) != state.maxSelections
                                ) {
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

                                if(state.getDescription != null && state.getDescription?.invoke(index)?.isNotBlank() == true) {
                                    Row(
                                        modifier = Modifier.weight(1f, true).width(intrinsicSize = IntrinsicSize.Max),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                lastSelected.value = index
                                                detailsExpanded = !detailsExpanded
                                            },
                                            content = {
                                                Icon(
                                                    Icons.Default.Info,
                                                    "More Details"
                                                )
                                            },
                                            modifier = Modifier.padding(0.dp).size(15.dp)
                                        )
                                    }
                                }
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
        if(detailsExpanded) {
            Dialog(
                onDismissRequest = { detailsExpanded = !detailsExpanded }
            ) {
                state.getDescription?.let { getDesc ->
                    if (lastSelected.value != -1) {
                        Card(
                            modifier = Modifier.padding(vertical = 8.dp),
                            elevation = 10.dp
                        ) {
                            Column(
                                modifier = Modifier.
                                verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = getDesc(lastSelected.value),
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}