package gmail.loganchazdon.dndhelper.ui.character

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.fillMaxSize(0.97f)
        ) {
            Column() {
                Row() {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Row {
                            TextField(
                                value = viewModel.character?.observeAsState()?.value?.name ?: "",
                                onValueChange = {
                                    scope.launch(Dispatchers.IO) {
                                        viewModel.setName(it)
                                    }
                                },
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
                            Column {
                                Box(
                                    modifier = Modifier.fillMaxHeight(0.6f)
                                ){
                                    VariableOrientationView(
                                        isVertical = isVertical,
                                        verticalAlignment = Alignment.Top,
                                        arrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        Column(
                                            modifier = if(isVertical) {
                                                Modifier.fillMaxHeight(0.5f)
                                            } else {
                                                Modifier
                                            }
                                        ) {
                                            CharacterTextView(
                                                modifier = topModifier.fillMaxHeight(0.5f),
                                                name = "Personality Traits",
                                                value = viewModel.character?.observeAsState()?.value?.personalityTraits
                                                    ?: "",
                                                onChange = {
                                                    scope.launch(Dispatchers.IO) {
                                                        viewModel.setPersonalityTraits(it)
                                                    }
                                                }
                                            )

                                            CharacterTextView(
                                                modifier = topModifier.fillMaxHeight(),
                                                name = "Ideals",
                                                value = viewModel.character?.observeAsState()?.value?.ideals
                                                    ?: "",
                                                onChange = {
                                                    scope.launch(Dispatchers.IO) {
                                                        viewModel.setIdeals(it)
                                                    }
                                                }
                                            )
                                        }

                                        if (!isVertical)
                                            Spacer(Modifier.width(5.dp))

                                        Column {
                                            CharacterTextView(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(0.5f),
                                                name = "Bonds",
                                                value = viewModel.character?.observeAsState()?.value?.bonds
                                                    ?: "",
                                                onChange = {
                                                    scope.launch(Dispatchers.IO) {
                                                        viewModel.setBonds(it)
                                                    }
                                                }
                                            )

                                            CharacterTextView(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(),
                                                name = "Flaws",
                                                value = viewModel.character?.observeAsState()?.value?.flaws
                                                    ?: "",
                                                onChange = {
                                                    scope.launch(Dispatchers.IO) {
                                                        viewModel.setFlaws(it)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                CharacterTextView(
                                    modifier = Modifier.fillMaxSize(),
                                    name = "Notes",
                                    value = viewModel.character?.observeAsState()?.value?.notes
                                        ?: "",
                                    onChange = {
                                        scope.launch(Dispatchers.IO) {
                                            viewModel.setNotes(it)
                                        }
                                    }
                                )
                            }

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
                                        value = viewModel.character?.observeAsState()?.value?.personalityTraits
                                            ?: "",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setPersonalityTraits(it)
                                            }
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Ideals") },
                                        value = viewModel.character?.observeAsState()?.value?.ideals
                                            ?: "",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setIdeals(it)
                                            }
                                        }
                                    )
                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Bonds") },
                                        value = viewModel.character?.observeAsState()?.value?.bonds
                                            ?: "",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setBonds(it)
                                            }
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Flaws") },
                                        value = viewModel.character?.observeAsState()?.value?.flaws
                                            ?: "",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setFlaws(it)
                                            }
                                        }
                                    )

                                    TextField(
                                        modifier = modifier,
                                        label = { Text("Notes") },
                                        value = viewModel.character?.observeAsState()?.value?.notes
                                            ?: "",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.setNotes(it)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }


                    Column() {
                        if(isVertical) RestButton(viewModel = viewModel)

                        FeaturesAndTraitsView(
                            feats = viewModel.character?.observeAsState()?.value?.feats,
                            features = viewModel.characterFeatures.observeAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.5f),
                            items = viewModel.character?.observeAsState()?.value?.backpack?.allItems ?: listOf(),
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
                            languages = viewModel.character?.observeAsState()?.value?.languages ?: listOf(),
                            proficiencies = viewModel.character?.observeAsState()?.value?.proficiencies ?: listOf(),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}
