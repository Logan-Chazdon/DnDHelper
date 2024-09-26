package ui.homebrew

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import model.AbilityBonus
import model.AbilityBonusChoice
import model.repositories.CharacterRepository.Companion.statNames
import java.util.*

@Composable
fun AbilityBonusesAndFeatsView(
    abilityBonuses: SnapshotStateList<AbilityBonus>,
    abilityBonusChoice: MutableState<AbilityBonusChoice?>
) {
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
                    statNames.forEachIndexed { index, it ->
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
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                singleLine = true
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
                                    abilityBonusChoice.value =
                                        AbilityBonusChoice(
                                            choose = try {
                                                choose.toInt()
                                            } catch (_: NumberFormatException) {
                                                1
                                            },
                                            from = stats.let {
                                                val result =
                                                    LinkedList<AbilityBonus>()
                                                var i = 0
                                                it.forEach { (stat, bonus) ->
                                                    if (enableStatMap[i] == true) {
                                                        result += AbilityBonus(
                                                            ability = stat,
                                                            bonus = try {
                                                                bonus.toInt()
                                                            } catch (_: NumberFormatException) {
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
                                    abilityBonuses.clear()
                                    abilityBonuses.addAll(
                                        stats.let {
                                            val result =
                                                mutableListOf<AbilityBonus>()
                                            var i = 0
                                            it.forEach { (stat, bonus) ->
                                                if (enableStatMap[i] == true) {
                                                    result += AbilityBonus(
                                                        ability = stat,
                                                        bonus = try {
                                                            bonus.toInt()
                                                        } catch (_: NumberFormatException) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Ability bonuses and feats",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(5.dp)
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
            abilityBonuses.forEach {
                Text(text = it.toString())
            }
            abilityBonusChoice.value?.let { choice ->
                Text("Choose ${choice.choose} from")
                choice.from.forEach {
                    Text(text = it.toString())
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