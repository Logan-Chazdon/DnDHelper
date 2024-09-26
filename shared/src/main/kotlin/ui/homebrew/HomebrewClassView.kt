package ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.newCharacter.AutoSave

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
                        CastingModAndSpellsView(
                            allSpells = viewModel.allSpells,
                            spells = viewModel.pactMagicSpells,
                            ability = viewModel.pactMagicAbility
                        )

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
                                            modifier = Modifier.weight(weight = 1f, fill = true),
                                            singleLine = true
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
                                            modifier = Modifier.weight(weight = 1f, fill = true),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicCantripsKnown[index],
                                            onValueChange = {
                                                viewModel.pactMagicCantripsKnown[index] = it
                                            },
                                            label = {
                                                Text("Cantrips")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = viewModel.pactMagicSpellsKnown[index],
                                            onValueChange = {
                                                viewModel.pactMagicSpellsKnown[index] = it
                                            },
                                            label = {
                                                Text("Spells")
                                            },
                                            modifier = Modifier.weight(weight = 1f, fill = true),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                //Spell casting
                item {
                    AttributeView(title = "Spell Casting", active = viewModel.hasSpellCasting) {
                        CastingModAndSpellsView(
                            allSpells = viewModel.allSpells,
                            spells = viewModel.spellCastingSpells,
                            ability = viewModel.spellCastingAbility
                        )

                        AttributeView(
                            title = "Prepares Spells",
                            active = viewModel.spellCastingPrepares
                        ) {
                            Row {
                                OutlinedTextField(
                                    value = viewModel.spellCastingCastingModMulti.value,
                                    onValueChange = {
                                        viewModel.spellCastingCastingModMulti.value = it
                                    },
                                    leadingIcon = { Text("*") },
                                    label = { Text(viewModel.spellCastingAbility.value + " Modifier") },
                                    modifier = Modifier.weight(1f, true)
                                )

                                OutlinedTextField(
                                    value = viewModel.spellCastingLevelMulti.value,
                                    onValueChange = {
                                        viewModel.spellCastingLevelMulti.value = it
                                    },
                                    leadingIcon = { Text("*") },
                                    label = { Text("Level") },
                                    modifier = Modifier.weight(1f, true)
                                )
                            }
                        }


                        AttributeView(
                            title = "Learns Spells",
                            active = viewModel.spellCastingLearnsSpells
                        ) {}

                        AttributeView(
                            title = "Is Half Caster",
                            active = viewModel.spellCastingIsHalfCaster
                        ) {}

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeightIn(min = 0.dp, max = 375.dp),
                            state = rememberLazyListState()
                        ) {
                            fun pointView(index: Int) {
                                item {
                                    Row {
                                        OutlinedTextField(
                                            value = viewModel.spellCastingSpellsAndCantripsKnown.keys.elementAt(index),
                                            onValueChange = {
                                                val value= viewModel.spellCastingSpellsAndCantripsKnown.values.elementAt(index)
                                                val key = viewModel.spellCastingSpellsAndCantripsKnown.keys.elementAt(index)
                                                viewModel.spellCastingSpellsAndCantripsKnown.remove(key)
                                                viewModel.spellCastingSpellsAndCantripsKnown[it] =
                                                    value
                                            },
                                            modifier = Modifier.weight(1f, true),
                                            label = {
                                                Text("Level")
                                            }
                                        )

                                        OutlinedTextField(
                                            value = viewModel.spellCastingSpellsAndCantripsKnown.values.elementAt(index).first,
                                            onValueChange = {
                                                val old = viewModel.spellCastingSpellsAndCantripsKnown.values.elementAt(index)
                                                val key = viewModel.spellCastingSpellsAndCantripsKnown.keys.elementAt(index)
                                                viewModel.spellCastingSpellsAndCantripsKnown[key] = old.copy(first =  it)
                                            },
                                            modifier = Modifier.weight(1f, true),
                                            label = {
                                                Text("Cantrips")
                                            }
                                        )

                                        if (viewModel.spellCastingLearnsSpells.value) {
                                            OutlinedTextField(
                                                value = viewModel.spellCastingSpellsAndCantripsKnown.values.elementAt(index).second,
                                                onValueChange = {
                                                    val old = viewModel.spellCastingSpellsAndCantripsKnown.values.elementAt(index)
                                                    val key = viewModel.spellCastingSpellsAndCantripsKnown.keys.elementAt(index)
                                                    viewModel.spellCastingSpellsAndCantripsKnown[key] = old.copy(second =  it)
                                                },
                                                modifier = Modifier.weight(1f, true),
                                                label = {
                                                    Text("Spells")
                                                }
                                            )
                                        }
                                    }
                                }

                                if(viewModel.spellCastingSpellsAndCantripsKnown.keys.size <= index) {
                                    viewModel.spellCastingSpellsAndCantripsKnown["20"] =
                                        viewModel.spellCastingSpellsAndCantripsKnown.values.last()
                                }

                                if (
                                    viewModel.spellCastingSpellsAndCantripsKnown.keys.elementAt(index) != "20"
                                ) {
                                    pointView(index + 1)
                                }
                            }

                            pointView(0)
                        }


                        Text("Spell slots by level")
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
                                            modifier = Modifier.weight(weight = 0.3f, fill = true),
                                            style = MaterialTheme.typography.subtitle2
                                        )
                                        for (i in 0 until 9) {
                                            OutlinedTextField(
                                                value = viewModel.spellCastingSlots[index].getOrNull(
                                                    i
                                                ) ?: "0",
                                                onValueChange = {
                                                    viewModel.spellCastingSlots[index][i] = it
                                                },
                                                label = {
                                                    Text((i + 1).toString())
                                                },
                                                modifier = Modifier.weight(
                                                    weight = 1f,
                                                    fill = true
                                                ),
                                                singleLine = true,
                                                textStyle = TextStyle(fontSize = 12.sp)
                                            )
                                        }
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
                                onClick = {
                                    navController.navigate("homebrewView/homebrewSubclassView/${viewModel.subclasses!!.value!![it].subclassId}")
                                },
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
                                    scope.launch(Dispatchers.IO) {
                                        val id = viewModel.createDefaultSubclass()
                                        Handler(mainLooper).post {
                                            navController.navigate("homebrewView/homebrewSubclassView/$id")
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}