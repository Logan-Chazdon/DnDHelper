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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.AbilityBonusChoice
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
                    val id = viewModel.createDefaultFeature()
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
                        OutlinedTextField(
                            value = viewModel.speed.value,
                            onValueChange = { viewModel.speed.value = it },
                            singleLine = true,
                            label = { Text("Speed") },
                            modifier = Modifier.weight(1f, true),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = {
                                Text("ft")
                            }
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

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
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
                    if (expanded) {
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
                            val stats = remember {
                                mutableStateMapOf<String, String>()
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 15.dp, end = 15.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 5.dp)
                                        ) {
                                            Text(
                                                text = "Contains choice",
                                                style = MaterialTheme.typography.h6
                                            )
                                            Checkbox(
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
                                    val enableStatMap = remember {
                                        mutableStateMapOf(
                                            0 to true,
                                            1 to true,
                                            2 to true,
                                            3 to true,
                                            4 to true,
                                            5 to true
                                        )
                                    }
                                    Repository.statNames.forEachIndexed { index, it ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Checkbox(
                                                checked = enableStatMap[index]!!,
                                                onCheckedChange = {
                                                    enableStatMap[index] = it
                                                }
                                            )
                                            LaunchedEffect(true) {
                                                stats[it] = "1"
                                            }

                                            OutlinedTextField(
                                                modifier = Modifier.fillMaxWidth(0.4f),
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Add,
                                                        "Plus"
                                                    )
                                                },
                                                value = stats[it] ?: "1",
                                                onValueChange = { value ->
                                                    stats[it] = value
                                                },
                                            )

                                            Text(text = it, style = MaterialTheme.typography.h6)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                expanded = false
                                                if (containsChoice) {
                                                    viewModel.abilityBonusChoice.value =
                                                        AbilityBonusChoice(
                                                            choose = try {
                                                                choose.toInt()
                                                            } catch (_: java.lang.NumberFormatException) {
                                                                1
                                                            },
                                                            from = stats.let {
                                                                val result =
                                                                    LinkedList<AbilityBonus>()
                                                                var i = 0
                                                                it.forEach{ (stat, bonus) ->
                                                                    if(enableStatMap[i] == true) {
                                                                        result += AbilityBonus(
                                                                            ability = stat,
                                                                            bonus = try {
                                                                                bonus.toInt()
                                                                            } catch (_: java.lang.NumberFormatException) {
                                                                                1
                                                                            }
                                                                        )
                                                                    }
                                                                    i++
                                                                }
                                                                result
                                                            }
                                                        )
                                                } else {
                                                    viewModel.abilityBonuses.clear()
                                                    viewModel.abilityBonuses.addAll(
                                                        stats.let {
                                                            val result =
                                                                mutableListOf<AbilityBonus>()
                                                            var i = 0
                                                            it.forEach { (stat, bonus) ->
                                                                if(enableStatMap[i] == true) {
                                                                    result += AbilityBonus(
                                                                        ability = stat,
                                                                        bonus = try {
                                                                            bonus.toInt()
                                                                        } catch (_: java.lang.NumberFormatException) {
                                                                            1
                                                                        }
                                                                    )
                                                                }
                                                                i++
                                                            }
                                                            result
                                                        }
                                                    )
                                                }
                                            }
                                        ) {
                                            Text("DONE")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(start = 5.dp)
                    ) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Ability bonuses and feats",
                                    style = MaterialTheme.typography.h6
                                )

                                //TODO implement race feats
                                /*
                                Text(
                                    text= "Grants feat"
                                )
                                Switch(
                                    checked = true,
                                    onCheckedChange = { }
                                )
                                */
                            }
                            viewModel.abilityBonuses.forEach {
                                Text(text= it.toString())
                            }
                            viewModel.abilityBonusChoice.value?.let { choice ->
                                Text("Choose ${choice.choose} from")
                                choice.from.forEach {
                                    Text(text= it.toString())
                                }
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
                    if (it.isNotEmpty()) {
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