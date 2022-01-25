package com.example.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dndhelper.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun CombatView(viewModel: CombatViewModel) {
    var hpPopUpExpanded by remember { mutableStateOf(false) }
    var hpPopUpMode by remember { mutableStateOf("heal" ) }
    val scope = rememberCoroutineScope()

    if(hpPopUpExpanded) {
        Dialog(onDismissRequest = { hpPopUpExpanded = false }) {
            Card(
                elevation = 5.dp
            ) {
                Column() {
                    var onClick : () -> Unit = {}
                    var temp by remember { mutableStateOf("") }
                    when(hpPopUpMode) {
                        "addTemp" -> {
                            Text("Add temporary HP")
                            onClick = {
                                hpPopUpExpanded = false
                                GlobalScope.launch() {
                                    viewModel.addTemp(temp)
                                }
                            }
                        }
                        "heal" -> {
                            Text("Heal")
                            onClick = {
                                hpPopUpExpanded = false
                                GlobalScope.launch() {
                                    viewModel.heal(temp)
                                }
                            }
                        }
                        "damage" -> {
                            Text("Damage")
                            onClick = {
                                hpPopUpExpanded = false
                                GlobalScope.launch() {
                                    viewModel.damage(temp)
                                }
                            }
                        }
                    }
                    TextField(
                        value = temp,
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Number),
                        onValueChange = { temp = it }
                    )
                    //TODO left align
                    Button(onClick = onClick) {
                        when(hpPopUpMode) {
                            "addTemp" -> {
                                Text("Add")
                            }
                            "heal" -> {
                                Text("Heal")
                            }
                            "damage" -> {
                                Text("Damage")
                            }
                        }
                    }
                }
            }
        }
    }


    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        val character = viewModel.character?.observeAsState()

        HeathStatsView(
            currentHp = viewModel.character?.observeAsState()?.value?.currentHp,
            maxHp = viewModel.character?.observeAsState()?.value?.maxHp,
            tempHp = viewModel.character?.observeAsState()?.value?.tempHp,
            addTemp = {
                hpPopUpExpanded = true
                hpPopUpMode = "addTemp"
            },
            heal = {
                hpPopUpExpanded = true
                hpPopUpMode = "heal"
            },
            damage = {
                hpPopUpExpanded = true
                hpPopUpMode = "damage"
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(
                modifier = Modifier.size(100.dp),
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "AC"
                    )
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_armour_class),
                            "",
                            Modifier.size(75.dp)
                        )
                        val ac = character?.value?.equiptArmor?.getAC(character.value?.getStatMod("Dex") ?: 10)
                        Text(
                            text = "$ac",
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                    }
                }
            }

            CombatListView(name = "Conditions", list = viewModel.character?.observeAsState()?.value?.conditions)

            CombatListView(name = "Resistance", list = viewModel.character?.observeAsState()?.value?.resistances)

        }


        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            DeathSavesView(
                type = "Success",
                num = viewModel.character?.observeAsState()?.value?.positiveDeathSaves,
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.updateDeathSaveSuccesses(it)
                    }
                }
            )

            DeathSavesView(
                type = "Fail",
                num = viewModel.character?.observeAsState()?.value?.negativeDeathSaves,
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.updateDeathSaveFailures(it)
                    }
                }
            )

        }


        Card (
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(5.dp),
            elevation = 2.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Box(
                    Modifier.width((LocalConfiguration.current.screenWidthDp - 20).dp)
                ) {
                    character?.value?.let { SpellCastingView(character = it) }
                }

                Spacer(Modifier.width(10.dp))

                //TODO replace this with items and features
                Box(
                    Modifier.width((LocalConfiguration.current.screenWidthDp - 20).dp)
                ) {
                    character?.value?.let { SpellCastingView(character = it) }
                }

            }
        }
    }
}