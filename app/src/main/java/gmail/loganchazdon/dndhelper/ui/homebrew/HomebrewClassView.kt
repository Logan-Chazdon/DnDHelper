package gmail.loganchazdon.dndhelper.ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.ui.SpellDetailsView
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomebrewClassView(
    viewModel: HomebrewClassViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val mainLooper = Looper.getMainLooper()
    val clazz = viewModel.clazz?.observeAsState()

    AutoSave(
        "homebrewClassView",
        { id ->
            viewModel.saveClass()
            id.value = viewModel.id
        },
        navController,
        true
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch(Dispatchers.IO) {
                    val id = viewModel.createDefaultFeature()
                    Handler(mainLooper).post {
                        navController.navigate("homebrewView/homebrewFeature/$id")
                    }
                }
            }) {
                Icon(Icons.Default.Add, "New Feature")
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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
                            Text(text = "Homebrew class name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h5
                    )
                }

                //Hit die
                item {
                    OutlinedTextField(
                        value = viewModel.hitDie.value,
                        onValueChange = {
                            viewModel.hitDie.value = it
                        },
                        label = {
                            Text("Hit die")
                        },
                        leadingIcon = {
                            Text("1d")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                //Gold
                item {
                    Column {
                        Text("Starting gold")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            OutlinedTextField(
                                value = viewModel.goldDie.value,
                                onValueChange = {
                                    viewModel.goldDie.value = it
                                },
                                label = {
                                    Text("Die")
                                },
                                trailingIcon = {
                                    Text("d4")
                                },
                                modifier = Modifier.weight(weight = 1f, fill = true)
                            )

                            OutlinedTextField(
                                value = viewModel.goldMultiplier.value,
                                onValueChange = {
                                    viewModel.goldMultiplier.value = it
                                },
                                label = {
                                    Text("Multiplier")
                                },
                                modifier = Modifier.weight(weight = 1f, fill = true)
                            )
                        }
                    }
                }

                //Pact magic
                item {
                    AttributeView(title = "Pact Magic", active = viewModel.hasPactMagic) {
                        //Casing mod
                        var expanded by remember {
                            mutableStateOf(false)
                        }
                        val spellsExpanded = remember {
                            mutableStateOf(false)
                        }

                        GenericSelectionPopupView(
                            isExpanded = spellsExpanded,
                            onItemClick = {
                                if(viewModel.pactMagicSpells.contains(it)) {
                                    viewModel.pactMagicSpells.remove(it)
                                } else {
                                    viewModel.pactMagicSpells.add(it)
                                }
                            },
                            items = viewModel.allSpells.observeAsState(emptyList()).value,
                            detailsView = {
                                SpellDetailsView(spell = it)
                            },
                            getName = {
                                it.name
                            },
                            isSelected = {
                                viewModel.pactMagicSpells.contains(it)
                            }
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                        ) {
                            Row {
                                Text(
                                    text = viewModel.pactMagicAbility.value,
                                    modifier = Modifier.padding(5.dp).weight(1f),
                                    style = MaterialTheme.typography.h5
                                )

                                Button(
                                    onClick = {
                                        spellsExpanded.value= !spellsExpanded.value
                                    }
                                ) {
                                    Text("ADD SPELLS")
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            Repository.statNames.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.pactMagicAbility.value = it
                                        expanded = false
                                    }
                                ) {
                                    Text(it)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.height(275.dp)
                        ) {
                            LazyColumn {
                                items(20) { index ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = (index + 1).toString(),
                                            modifier = Modifier.weight(weight = 0.4f, fill = true),
                                            style = MaterialTheme.typography.h6
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicSlots[index].first,
                                            onValueChange = {
                                                viewModel.pactMagicSlots[index] =
                                                    Pair(it, viewModel.pactMagicSlots[index].second)
                                            },
                                            label = {
                                                Text("Slot level")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true)
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicSlots[index].second,
                                            onValueChange = {
                                                viewModel.pactMagicSlots[index] =
                                                    Pair(viewModel.pactMagicSlots[index].first, it)
                                            },
                                            label = {
                                                Text("Amount")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true)
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicCantripsKnown[index],
                                            onValueChange = {
                                                viewModel.pactMagicCantripsKnown[index] = it
                                            },
                                            label = {
                                                Text("Cantrips")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true)
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicSpellsKnown[index] ,
                                            onValueChange = {
                                                viewModel.pactMagicSpellsKnown[index]  =it
                                            },
                                            label = {
                                                Text("Spells")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                //Features
                clazz?.value?.levelPath?.let {
                    if (it.isNotEmpty()) {
                        item {
                            FeaturesView(
                                features = it,
                                onDelete = { id ->
                                    viewModel.removeFeature(id)
                                },
                                onClick = { id ->
                                    navController.navigate("homebrewView/homebrewFeature/$id")
                                }
                            )
                        }
                    }
                }

                //Subclasses
                item {
                    Card {
                        Column {
                            Text(
                                text = "Subclasses"
                            )

                            OutlinedTextField(
                                value = viewModel.subclassLevel.value,
                                onValueChange = {
                                    viewModel.subclassLevel.value = it
                                },
                                label = {
                                    Text("Subclass level")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            GenericSelectionView(
                                chosen = viewModel.subclasses?.observeAsState(listOf())?.value.let {
                                    val result = mutableListOf<String>()
                                    it?.forEach {
                                        result.add(it.name)
                                    }
                                    result
                                },
                                onDelete = {
                                    viewModel.deleteSubclass(it)
                                },
                                onExpanded = {
                                    //TODO navigate
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}