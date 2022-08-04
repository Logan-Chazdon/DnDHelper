package gmail.loganchazdon.dndhelper.ui.character

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import gmail.loganchazdon.dndhelper.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@Composable
fun CharacterMainView( viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    val isVertical =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    //Update all data whenever it is emitted from the database.
    //TODO find a more dry way to do this.
    LaunchedEffect(viewModel.character.value?.personalityTraits) {
        viewModel.personalityTraits.value =
            viewModel.character.value?.personalityTraits ?: ""
    }
    LaunchedEffect(viewModel.character.value?.name) {
        viewModel.name.value =
            viewModel.character.value?.name?: ""
    }
    LaunchedEffect(viewModel.character.value?.bonds) {
        viewModel.bonds.value =
            viewModel.character.value?.bonds?: ""
    }
    LaunchedEffect(viewModel.character.value?.flaws) {
        viewModel.flaws.value =
            viewModel.character.value?.flaws?: ""
    }
    LaunchedEffect(viewModel.character.value?.notes) {
        viewModel.notes.value =
            viewModel.character.value?.notes?: ""
    }
    LaunchedEffect(viewModel.character.value?.ideals) {
        viewModel.ideals.value =
            viewModel.character.value?.ideals?: ""
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
                                onValueChange = { viewModel.name.value = it},
                            )
                            if(!isVertical) RestButton(viewModel = viewModel)
                        }
                        val gridNotRowFlow: Flow<Boolean> = LocalContext.current.dataStore.data.let {
                            remember {
                                it.map { preferences ->
                                    preferences[booleanPreferencesKey("grid_not_row")] ?: false
                                }
                            }
                        }

                        if (!gridNotRowFlow.collectAsState(false).value) {
                            val topModifier = if (isVertical) {
                                Modifier
                            } else {
                                Modifier.fillMaxWidth(0.5f)
                            }
                            ReorderingColumnView(
                                { modifier : Modifier, _: ColumnScope ->
                                    Box(modifier = Modifier.fillMaxHeight(0.6f).then(modifier)) {
                                        VariableOrientationView(
                                            isVertical = isVertical,
                                            verticalAlignment = Alignment.Top,
                                            arrangement = Arrangement.spacedBy(0.dp)
                                        ) {
                                            Box(
                                                modifier = if(isVertical) {
                                                    Modifier.fillMaxHeight(0.5f)
                                                } else {
                                                    Modifier
                                                }
                                            ) {
                                                ReorderingColumnView(
                                                    { modifier : Modifier, _: ColumnScope ->
                                                        CharacterTextView(
                                                            modifier = topModifier.then(modifier),
                                                            name = "Personality Traits",
                                                            value = viewModel.personalityTraits.collectAsState().value,
                                                            onChange = { viewModel.personalityTraits.value = it}
                                                        )
                                                    },
                                                    { modifier : Modifier, _: ColumnScope ->
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
                                                { modifier : Modifier, _: ColumnScope ->
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
                                                { modifier : Modifier, _: ColumnScope ->
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
                                { modifier : Modifier, _: ColumnScope ->
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
                            //Row
                            //TODO make this snap.
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .padding(5.dp),
                                elevation = 2.dp,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                val width = ((LocalConfiguration.current.screenWidthDp + 20) / 2).dp
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    val modifier = Modifier
                                        .fillMaxHeight()
                                        .width(width)
                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Personality Traits") },
                                        value = viewModel.personalityTraits.collectAsState().value,
                                        onValueChange = {
                                            viewModel.personalityTraits.value = it
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Ideals") },
                                        value = viewModel.ideals.collectAsState().value,
                                        onValueChange = {
                                            viewModel.ideals.value = it
                                        }
                                    )
                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Bonds") },
                                        value = viewModel.bonds.collectAsState().value,
                                        onValueChange = {
                                            viewModel.bonds.value = it
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Flaws") },
                                        value = viewModel.flaws.collectAsState().value,
                                        onValueChange = {
                                            viewModel.flaws.value = it
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Notes") },
                                        value = viewModel.notes.collectAsState().value,
                                        onValueChange = {
                                            viewModel.notes.value = it
                                        }
                                    )
                                }
                            }
                        }
                    }


                    Column {
                        if(isVertical) RestButton(viewModel = viewModel)

                        FeaturesAndTraitsView(
                            feats = viewModel.character.observeAsState().value?.feats,
                            features = viewModel.characterFeatures.observeAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.5f),
                            items = viewModel.character.observeAsState().value?.backpack?.allItems ?: listOf(),
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
                            languages = viewModel.character.observeAsState().value?.languages?.distinct() ?: listOf(),
                            proficiencies = viewModel.character.observeAsState().value?.proficiencies?.distinct() ?: listOf(),
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
    vararg content: @Composable (modifier : Modifier, ColumnScope) -> Unit
) {
    val mutableContent = remember {
        mutableStateListOf<@Composable (modifier : Modifier, ColumnScope) -> Unit>().run {
            this.addAll(content)
            this
        }
    }
    Column {
        mutableContent.forEachIndexed { index, view ->
            view(
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged {
                        if (it.hasFocus && index != 0) {
                            mutableContent.removeAt(index)
                            mutableContent.add(0, view)
                        }
                    },
                this
            )
        }
    }
}
