package ui.character

//import dataStore

import Platform
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform
import ui.platformSpecific.IO
import ui.platformSpecific.isVertical
import ui.preferences.DataStore
import ui.subcomposables.MultipageView


@Composable
fun CharacterMainView(viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    val isVertical = isVertical()

    scope.launch {
        viewModel.character.collect {
            viewModel.characterFeatures.value = it.displayFeatures
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.fillMaxSize(0.97f)
        ) {
            Column {
                Row {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Row {
                            TextField(
                                value = viewModel.name.collectAsState().value,
                                onValueChange = { viewModel.name.value = it },
                            )
                            if (!isVertical) RestButton(viewModel = viewModel)
                        }
                        val gridNotRowFlow = DataStore.gridNotRow()

                        if (!gridNotRowFlow.collectAsState(false).value) {
                            val topModifier = if (isVertical) {
                                Modifier
                            } else {
                                Modifier.fillMaxWidth(0.5f)
                            }

                            val reorder: Boolean = remember {
                                when (platform) {
                                    Platform.Web -> false
                                    Platform.Android -> true
                                }
                            }

                            ReorderingColumnView(
                                reorder,
                                { modifier: Modifier, _: ColumnScope ->
                                    Box(modifier = Modifier.fillMaxHeight(0.6f).then(modifier)) {
                                        VariableOrientationView(
                                            isVertical = isVertical,
                                            verticalAlignment = Alignment.Top,
                                            arrangement = Arrangement.spacedBy(0.dp)
                                        ) {
                                            Box(
                                                modifier = if (isVertical) {
                                                    Modifier.fillMaxHeight(0.5f)
                                                } else {
                                                    Modifier
                                                }
                                            ) {
                                                ReorderingColumnView(
                                                    reorder,
                                                    { modifier: Modifier, _: ColumnScope ->
                                                        CharacterTextView(
                                                            modifier = topModifier.then(modifier),
                                                            name = "Personality Traits",
                                                            value = viewModel.personalityTraits.collectAsState().value,
                                                            onChange = { viewModel.personalityTraits.value = it }
                                                        )
                                                    },
                                                    { modifier: Modifier, _: ColumnScope ->
                                                        CharacterTextView(
                                                            modifier = topModifier.then(modifier),
                                                            name = "Ideals",
                                                            value = viewModel.ideals.collectAsState().value,
                                                            onChange = {
                                                                viewModel.ideals.value = it
                                                            }
                                                        )
                                                    }
                                                )
                                            }

                                            if (!isVertical)
                                                Spacer(Modifier.width(5.dp))

                                            ReorderingColumnView(
                                                reorder,
                                                { modifier: Modifier, _: ColumnScope ->
                                                    CharacterTextView(
                                                        modifier = modifier
                                                            .fillMaxWidth(),
                                                        name = "Bonds",
                                                        value = viewModel.bonds.collectAsState().value,
                                                        onChange = {
                                                            viewModel.bonds.value = it
                                                        }
                                                    )
                                                },
                                                { modifier: Modifier, _: ColumnScope ->
                                                    CharacterTextView(
                                                        modifier = modifier
                                                            .fillMaxWidth(),
                                                        name = "Flaws",
                                                        value = viewModel.flaws.collectAsState().value,
                                                        onChange = {
                                                            viewModel.flaws.value = it
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                    }
                                },
                                { modifier: Modifier, _: ColumnScope ->
                                    CharacterTextView(
                                        modifier = Modifier.fillMaxSize().weight(0.7f).then(modifier),
                                        name = "Notes",
                                        value = viewModel.notes.collectAsState().value,
                                        onChange = {
                                            viewModel.notes.value = it
                                        }
                                    )
                                }
                            )

                        } else {
                            MultipageView(
                                modifier = Modifier.fillMaxSize(),
                                pages = arrayOf(
                                    @Composable { modifier ->
                                        TextField(
                                            modifier = modifier,
                                            label = { Text("Personality Traits") },
                                            value = viewModel.personalityTraits.collectAsState().value,
                                            onValueChange = {
                                                viewModel.personalityTraits.value = it
                                            }
                                        )
                                    },
                                    @Composable { modifier ->
                                        TextField(
                                            modifier = modifier,
                                            label = { Text("Ideals") },
                                            value = viewModel.ideals.collectAsState().value,
                                            onValueChange = {
                                                viewModel.ideals.value = it
                                            }
                                        )
                                    },
                                    @Composable { modifier ->
                                        TextField(
                                            modifier = modifier,
                                            label = { Text("Bonds") },
                                            value = viewModel.bonds.collectAsState().value,
                                            onValueChange = {
                                                viewModel.bonds.value = it
                                            }
                                        )
                                    },
                                    @Composable { modifier ->
                                        TextField(
                                            modifier = modifier,
                                            label = { Text("Flaws") },
                                            value = viewModel.flaws.collectAsState().value,
                                            onValueChange = {
                                                viewModel.flaws.value = it
                                            }
                                        )
                                    },
                                    @Composable { modifier ->
                                        TextField(
                                            modifier = modifier,
                                            label = { Text("Notes") },
                                            value = viewModel.notes.collectAsState().value,
                                            onValueChange = {
                                                viewModel.notes.value = it
                                            }
                                        )
                                    }
                                )
                            )

                        }
                    }


                    Column {
                        if (isVertical) RestButton(viewModel = viewModel)

                        FeaturesAndTraitsView(
                            feats = viewModel.character.collectAsState().value.feats,
                            features = viewModel.characterFeatures.collectAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.5f),
                            items = viewModel.character.collectAsState().value.backpack.allItems,
                            infuse = { infusion, item ->
                                scope.launch(Dispatchers.IO) {
                                    viewModel.infuse(item, infusion)
                                }
                            },
                            disableInfusion = { infusion ->
                                scope.launch(Dispatchers.IO) {
                                    viewModel.disableInfusion(infusion)
                                }
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        LanguagesAndProficienciesView(
                            languages = viewModel.character.collectAsState().value.languages.distinct(),
                            proficiencies = viewModel.character.collectAsState().value.proficiencies.distinct(),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReorderingColumnView(
    reorder: Boolean = true,
    vararg content: @Composable (modifier: Modifier, ColumnScope) -> Unit
) {
    val mutableContent = remember {
        mutableStateListOf<@Composable (modifier: Modifier, ColumnScope) -> Unit>().run {
            this.addAll(content)
            this
        }
    }
    Column {
        mutableContent.forEachIndexed { index, view ->
            view(
                Modifier
                    .weight(1f)
                    .onFocusChanged {
                        if (reorder && it.hasFocus && index != 0) {
                            mutableContent.removeAt(index)
                            mutableContent.add(0, view)
                        }
                    },
                this
            )
        }
    }
}
