package gmail.loganchazdon.dndhelper.ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomebrewRaceView(
    navController: NavController?,
    viewModel: HomebrewRaceViewModel
) {
    val scope = rememberCoroutineScope()
    val mainLooper = Looper.getMainLooper()
    val race = viewModel.race.observeAsState()

    navController?.let {
        AutoSave(
        "homebrewRaceView",
        { id ->
            viewModel.saveRace()
            id.value = viewModel.id
        },
            it,
        true
    )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch(Dispatchers.IO) {
                    val id  =viewModel.createDefaultFeature()
                    Handler(mainLooper).post {
                        navController?.navigate("homebrewView/homebrewFeature/$id")
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
                            Text(text = "Homebrew race name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h5
                    )
                }

                //Speed and size
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    //TODO add ft.
                        OutlinedTextField(
                            value = viewModel.speed.value,
                            onValueChange = {viewModel.speed.value = it},
                            singleLine = true,
                            label = { Text("Speed") },
                            modifier = Modifier.weight(1f, true)
                        )

                        var expanded by remember {
                            mutableStateOf(false)
                        }

                        Card(
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                                .weight(1f, true)
                                .fillMaxHeight()
                                .padding(top = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = viewModel.sizeClass.value,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.h6
                                )
                            }

                            DropdownMenu(expanded = expanded , onDismissRequest = { expanded= false }) {
                                viewModel.sizeClassOptions.forEach {
                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        viewModel.sizeClass.value = it
                                    }) {
                                        Text(
                                            text = it
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                //Ability bonuses and feats
                item {
                    var expanded by remember {
                        mutableStateOf(false)
                    }
                    if(expanded) {
                        Dialog(
                            onDismissRequest = { expanded = false },
                            properties = DialogProperties(
                                usePlatformDefaultWidth = false,
                                dismissOnClickOutside = true,
                                dismissOnBackPress = true
                            )
                        ) {
                            var containsChoice by remember {
                                mutableStateOf(false)
                            }
                            var choose by remember {
                                mutableStateOf("")
                            }
                            Card(
                                modifier = Modifier.fillMaxSize(0.8f)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 15.dp, end = 15.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Contains choice"
                                            )
                                            Switch(
                                                checked = containsChoice,
                                                onCheckedChange = { containsChoice = it }
                                            )
                                        }

                                        OutlinedTextField(
                                            value = choose,
                                            onValueChange = {
                                                choose = it
                                            },
                                            enabled = containsChoice,
                                            label = {
                                                Text("Choose")
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        expanded = !expanded
                                    }
                                ) {
                                    Text("ADD")
                                }
                            }
                        }
                    }
                }

                //Features
                race.value?.traits?.let {
                    if(it.isNotEmpty()) {
                        item {
                            FeaturesView(
                                features = it,
                                onDelete = { id ->
                                    viewModel.removeFeature(id)
                                },
                                onClick = { id ->
                                    navController?.navigate("homebrewView/homebrewFeature/$id")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}