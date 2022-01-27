package com.example.dndhelper.ui.character

import android.annotation.SuppressLint
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.dndhelper.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun CharacterMainView( viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
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
                        TextField(
                            value = viewModel.character?.observeAsState()?.value?.name ?: "",
                            onValueChange = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.setName(it)
                                }
                            },
                        )
                        val gridNotRowFlow: Flow<Boolean> = LocalContext.current.dataStore.data
                            .map { preferences ->
                                preferences[booleanPreferencesKey("grid_not_row")] ?: false
                            }
                        val isVertical =
                            LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                        if (!gridNotRowFlow.collectAsState(false).value) {
                            val topModifier = if (isVertical) {
                                Modifier
                            } else {
                                Modifier.fillMaxWidth(0.5f)
                            }
                            VariableOrientationView(
                                isVertical = isVertical,
                                verticalAlignment = Alignment.Top,
                                arrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Column {
                                    CharacterTextView(
                                        modifier = topModifier,
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
                                        modifier = topModifier,
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
                                        modifier = Modifier.fillMaxWidth(),
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
                                        modifier = Modifier.fillMaxWidth(),
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
                                    val modifier = Modifier.fillMaxHeight().width(width)
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
                                }
                            }
                        }
                    }


                    Column() {
                        FeaturesAndTraitsView(
                            features = viewModel.characterFeatures.observeAsState(listOf()).value,
                            modifier = Modifier.fillMaxHeight(0.5f)
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