package ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import gmail.loganchazdon.dndhelper.shared.generated.resources.Res
import gmail.loganchazdon.dndhelper.shared.generated.resources.ic_armour_class
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Spell
import org.jetbrains.compose.resources.painterResource
import ui.SpellDetailsView
import ui.platformSpecific.getScreenWidth
import ui.platformSpecific.isVertical


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@Composable
fun CombatView(viewModel: CombatViewModel) {
    var hpPopUpExpanded by remember { mutableStateOf(false) }
    var hpPopUpMode by remember { mutableStateOf("heal") }
    val scope = rememberCoroutineScope()
    if (hpPopUpExpanded) {
        Dialog(onDismissRequest = { hpPopUpExpanded = false }) {
            Card(
                elevation = 5.dp
            ) {
                Column(
                    modifier = Modifier.padding(5.dp)
                ) {
                    var onClick: () -> Unit = {}
                    var temp by remember { mutableStateOf("") }
                    val title = remember {
                        mutableStateOf("")
                    }
                    when (hpPopUpMode) {
                        "addTemp" -> {
                            title.value = "Add temporary HP"
                            onClick = {
                                hpPopUpExpanded = false
                                scope.launch(/*Dispatchers.IO*/) {
                                    try {
                                        viewModel.setTemp(temp)
                                    } catch (e: NumberFormatException) {
                                    }
                                }
                            }
                        }
                        "heal" -> {
                            title.value = "Heal"
                            onClick = {
                                hpPopUpExpanded = false
                                scope.launch(/*Dispatchers.IO*/) {
                                    try {
                                        viewModel.heal(temp)
                                    } catch (e: NumberFormatException) {
                                    }
                                }
                            }
                        }
                        "damage" -> {
                            title.value = "Damage"
                            onClick = {
                                hpPopUpExpanded = false
                                scope.launch(/*Dispatchers.IO*/) {
                                    try {
                                        viewModel.damage(temp)
                                    } catch (e: NumberFormatException) {
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = title.value,
                        style = MaterialTheme.typography.h6
                    )
                    val focusManager = LocalFocusManager.current
                    TextField(
                        value = temp,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onClick.invoke()
                                focusManager.clearFocus()
                            }
                        ),
                        onValueChange = { temp = it }
                    )

                    Button(
                        onClick = {
                            onClick.invoke()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        when (hpPopUpMode) {
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

    val character = viewModel.character.collectAsState()
    val isVertical = isVertical()
    VariableOrientationView(isVertical = isVertical, arrangement = Arrangement.SpaceBetween) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(
                if (isVertical) {
                    0.95f
                } else {
                    0.45f
                }
            )
        ) {
            viewModel.character.collectAsState().value?.let {
                HeathStatsView(
                    currentHp = it.currentHp,
                    maxHp = it.maxHp,
                    tempHp = it.tempHp,
                    setTemp = {
                        scope.launch(/*Dispatchers.IO*/) {
                            viewModel.setTemp(it)
                        }
                    },
                    setHp = {
                        scope.launch(/*Dispatchers.IO*/) {
                            viewModel.setHp(it)
                        }
                    },
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
            }

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
                    ) {
                        Text(
                            text = "AC"
                        )
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_armour_class),
                                "",
                                Modifier.size(75.dp)
                            )
                            val ac = character.value?.armorClass
                            Text(
                                text = "$ac",
                                modifier = Modifier.padding(bottom = 5.dp),
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.size(100.dp),
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Speed"
                        )
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier.size(75.dp)
                            )
                            Text(
                                text = character.value?.groundSpeed.toString(),
                                modifier = Modifier.padding(bottom = 5.dp),
                                style = MaterialTheme.typography.h5
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.size(100.dp),
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hit dice"
                        )
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier.size(75.dp)
                            )
                            Text(
                                text = character.value?.maxHitDice ?: "",
                                modifier = Modifier.padding(bottom = 5.dp),
                                style = MaterialTheme.typography.h5
                            )
                        }
                    }
                }

            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                DeathSavesView(
                    type = "Success",
                    num = viewModel.character.collectAsState().value?.positiveDeathSaves,
                    onClick = {
                        scope.launch(/*Dispatchers.IO*/) {
                            viewModel.updateDeathSaveSuccesses(it)
                        }
                    }
                )

                DeathSavesView(
                    type = "Fail",
                    num = viewModel.character.collectAsState().value?.negativeDeathSaves,
                    onClick = {
                        scope.launch(/*Dispatchers.IO*/) {
                            viewModel.updateDeathSaveFailures(it)
                        }
                    }
                )

            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(5.dp),
            elevation = 2.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            val width = if (isVertical) {
                getScreenWidth() - 20.dp
            } else {
                (getScreenWidth() - 20.dp) / 2
            }
            var castIsExpanded by remember { mutableStateOf(false) }
            var spell by remember { mutableStateOf<Spell?>(null) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                //This is used to force a recalculation of allSpells.
                val allSpellsKey = remember {
                    mutableStateOf(0)
                }
                val allSpells: State<Map<Int, List<Pair<Boolean?, Spell>>>> = produceState(emptyMap(), character.value, allSpellsKey.value)  {
                    this.launch(/*Dispatchers.IO*/) {
                        value = viewModel.getAllSpells()
                    }
                }
                if (allSpells.value.isNotEmpty()) {
                    Box(
                        Modifier.width(width)
                    ) {
                        character.value?.let {
                            SpellCastingView(
                                spellSlotsOffsetForCantrips = viewModel.getSpellSlotsAndCantrips(),
                                allSpells = allSpells.value,
                                cast = { newSpell ->
                                    spell = newSpell
                                    castIsExpanded = true
                                },
                                refundSlot = { slot ->
                                    scope.launch(/*Dispatchers.IO*/) {
                                        viewModel.refundSlot(slot)
                                    }
                                },
                                useSlot = { slot ->
                                    scope.launch(/*Dispatchers.IO*/) {
                                        viewModel.useSlot(slot)
                                    }
                                },
                                togglePreparation = { spell, prepared ->
                                    scope.launch(/*Dispatchers.IO*/) {
                                        viewModel.togglePreparation(spell, prepared)
                                        allSpellsKey.value = allSpellsKey.value + 1
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                Box(
                    Modifier.width(width)
                ) {
                    character.value?.let { ItemsAndAbilitiesView(character = it) }
                }
            }

            if (castIsExpanded) {
                Dialog(
                    onDismissRequest = { castIsExpanded = false },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        usePlatformDefaultWidth = false
                    )
                ) {
                    spell?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Column(
                                Modifier.padding(15.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SpellDetailsView(spell = it)


                                var level by remember { mutableStateOf(it.level) }
                                val levelText = viewModel.getCastingOptions(it)
                                    .findLast { it.first == level }?.second
                                    ?: viewModel.getCastingOptions(it)
                                        .findLast { it.first >= level }?.second
                                if (spell?.level != 0 && levelText != null) {
                                    var expanded by remember { mutableStateOf(false) }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card {
                                            Row(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .fillMaxWidth(0.5f)
                                                    .clickable { expanded = true },
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = levelText,
                                                    style = MaterialTheme.typography.h6
                                                )
                                                Icon(
                                                    Icons.Default.ArrowDropDown,
                                                    "Select spell Level"
                                                )
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            viewModel.getCastingOptions(it).forEach {
                                                DropdownMenuItem(onClick = {
                                                    expanded = false
                                                    level = it.first
                                                }) {
                                                    Text(it.second)
                                                }
                                            }
                                        }

                                        Button(onClick = {
                                            GlobalScope.launch {
                                                viewModel.cast(spell!!, level)
                                            }
                                            castIsExpanded = false
                                        }) {
                                            Text("CAST")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}