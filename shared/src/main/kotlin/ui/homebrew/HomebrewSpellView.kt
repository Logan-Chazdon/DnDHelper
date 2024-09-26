package ui.homebrew

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.newCharacter.AutoSave

@Composable
fun HomebrewSpellView(
    viewModel: HomebrewSpellViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val focusManager = LocalFocusManager.current
    AutoSave(
        "homebrewSpellView",
        { id ->
            viewModel.saveSpell()
            id.value = viewModel.id
        },
        navController,
        true
    )

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth(0.95f)
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Name
            item {
                OutlinedTextField(
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it },
                    placeholder = {
                        Text(text = "Homebrew spell name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.h5
                )
            }

            //Desc
            item {
                OutlinedTextField(
                    value = viewModel.desc.value,
                    onValueChange = {
                        viewModel.desc.value = it
                    },
                    label = {
                        Text("Spell description")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            //Spell level
            item {
                var isError = false

                OutlinedTextField(
                    value = viewModel.level.value,
                    onValueChange = {
                        viewModel.level.value = it
                        isError = viewModel.level.value.toIntOrNull() == null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    label = {
                        Text("Level")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }


            //Toggles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ComponentSwitch("Somatic", viewModel.hasSomatic)
                    ComponentSwitch("Verbal", viewModel.hasVerbal)
                    ComponentSwitch("Material", viewModel.hasMaterial)
                    ComponentSwitch("Ritual", viewModel.isRitual)
                }
            }

            //Item components
            item {
                if (viewModel.hasMaterial.value) {
                    OutlinedTextField(
                        value = viewModel.materials.value,
                        onValueChange = {
                            viewModel.materials.value = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Material Components")
                        },
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                }
            }

            //Damage
            item {
                OutlinedTextField(
                    value = viewModel.damage.value,
                    onValueChange = {
                        viewModel.damage.value = it
                    },
                    label = {
                        Text("Damage")
                    },
                    placeholder = {
                        Text("-")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }


            item {
                OutlinedTextField(
                    value = viewModel.range.value,
                    onValueChange = {
                        viewModel.range.value = it
                    },
                    label = {
                        Text("Range")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.area.value,
                    onValueChange = {
                        viewModel.area.value = it
                    },
                    label = {
                        Text("Area")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                OutlinedTextField(
                    value = viewModel.castingTime.value,
                    onValueChange = {
                        viewModel.castingTime.value = it
                    },
                    label = {
                        Text("Casting time")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.duration.value,
                    onValueChange = {
                        viewModel.duration.value = it
                    },
                    label = {
                        Text("Duration")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.school.value,
                    onValueChange = {
                        viewModel.school.value = it
                    },
                    label = {
                        Text("School")
                    },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                Text(text = "Classes", style=  MaterialTheme.typography.h5)
            }

            item {
                val expanded = mutableStateOf(false)
                GenericSelectionView(
                    chosen = viewModel.classes.observeAsState(emptyList()).value.map { it.name },
                    onDelete = {
                        scope.launch {
                            viewModel.removeClass(it)
                        }
                    },
                    onExpanded = { expanded.value = !expanded.value }
                )

                GenericSelectionPopupView(
                    items = viewModel.allClasses.observeAsState(emptyList()).value,
                    onItemClick = {
                        scope.launch {
                            viewModel.toggleClass(it)
                        }
                    },
                    detailsView = null,
                    isExpanded = expanded,
                    getName = { it.name },
                    isSelected = {
                        viewModel.classes.value?.firstOrNull { item -> item.id == it.id } != null
                    }
                )
            }
        }
    }
}

@Composable
private fun ComponentSwitch(name: String, checked: MutableState<Boolean>) {
    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked.value,
                onCheckedChange = {
                    checked.value = !checked.value
                }
            )
            Text(
                text = name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}