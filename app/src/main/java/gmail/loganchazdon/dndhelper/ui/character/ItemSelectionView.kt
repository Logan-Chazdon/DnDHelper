package gmail.loganchazdon.dndhelper.ui.character

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import gmail.loganchazdon.dndhelper.model.Armor
import gmail.loganchazdon.dndhelper.model.Item
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.Weapon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ItemSelectionView(
    allItems: List<ItemInterface>,
    onDismissRequest : () -> Unit,
    renderBuyButton: Boolean,
    canBuy: (ItemInterface) -> Boolean,
    onBuy: (ItemInterface) -> Unit,
    onAdd: (ItemInterface) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(10.dp),
                elevation = 10.dp
            ) {
                Column {
                    var selected by remember { mutableStateOf(-1) }
                    var search by remember { mutableStateOf("") }
                    Row(
                        Modifier
                            .fillMaxWidth(),
                    ) {
                        TextField(
                            value = search,
                            label = {
                                Text("Search")
                            },
                            onValueChange = {
                                search = it
                            },
                            singleLine = true,
                            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    "Search"
                                )
                            }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight(0.55f),
                        state = rememberLazyListState(),
                    ) {
                        itemsIndexed(allItems) { i, item ->
                            //TODO upgrade search
                            if (
                                search == "" ||
                                item.displayName.lowercase().contains(search.lowercase())
                            ) {
                                val color = if (selected == i) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.surface
                                }
                                Card(
                                    shape = RoundedCornerShape(5.dp),
                                    elevation = 2.dp,
                                    modifier = Modifier
                                        .clickable { selected = i }
                                        .fillMaxWidth(),
                                    backgroundColor = color
                                ) {
                                    Row(
                                        Modifier.padding(5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = item.displayName)
                                        Text(text = item.costString())
                                    }
                                }
                            }
                        }
                    }

                    //Custom item
                    var itemTypeExpanded by remember { mutableStateOf(false) }
                    val itemTypes = listOf(
                        "Normal Item",
                        "Armor",
                        "Weapon"
                    )
                    var customItemType by remember { mutableStateOf(0) }
                    Text(
                        text = itemTypes[customItemType],
                        modifier = Modifier
                            .clickable { itemTypeExpanded = true }
                            .fillMaxWidth(),
                        fontSize = 20.sp
                    )
                    DropdownMenu(
                        expanded = itemTypeExpanded,
                        onDismissRequest = { itemTypeExpanded = false }
                    ) {
                        for (i in 0..2) {
                            DropdownMenuItem(onClick = { customItemType = i }) {
                                Text(itemTypes[i])
                                selected = -1
                            }
                        }
                    }

                    var customWeaponDamage by remember { mutableStateOf("") }
                    var customWeaponDamageType by remember { mutableStateOf("") }
                    var customWeaponRange by remember { mutableStateOf("5ft") }
                    var baseAc by remember { mutableStateOf("") }
                    var maxAcFromDex by remember { mutableStateOf("") }
                    var customItemName by remember { mutableStateOf("") }
                    var stealth by remember { mutableStateOf(false) }
                    var proficiency by remember { mutableStateOf("") }

                    TextField(
                        value = customItemName,
                        onValueChange = {
                            customItemName = it
                        },
                        label = { Text("Custom item name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    when (customItemType) {
                        0 -> {
                            //Normal item
                        }
                        1 -> {
                            //Armor
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val textFieldSize =
                                    LocalConfiguration.current.screenWidthDp.dp * 0.45f
                                OutlinedTextField(
                                    value = baseAc,
                                    onValueChange = {
                                        baseAc = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    label = { Text("Base AC") },
                                    modifier = Modifier.width(textFieldSize)
                                )


                                OutlinedTextField(
                                    value = maxAcFromDex,
                                    onValueChange = {
                                        maxAcFromDex = it
                                    },
                                    label = { Text("Max AC from Dex") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(textFieldSize)
                                )
                            }
                            Row() {
                                Text("Disadvantage on stealth: ")
                                Checkbox(
                                    checked = stealth,
                                    onCheckedChange = {
                                        stealth = it
                                    },
                                )
                            }
                        }
                        2 -> {
                            //Weapon
                            val textFieldSize =
                                LocalConfiguration.current.screenWidthDp.dp * 0.45f
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = customWeaponDamage,
                                    onValueChange = {
                                        customWeaponDamage = it
                                    },
                                    label = {
                                        Text("Damage")
                                    },
                                    modifier = Modifier.width(textFieldSize)
                                )
                                OutlinedTextField(
                                    value = customWeaponDamageType,
                                    onValueChange = {
                                        customWeaponDamageType = it
                                    },
                                    label = {
                                        Text("Damage type")
                                    },
                                    modifier = Modifier.width(textFieldSize)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                OutlinedTextField(
                                    value = customWeaponRange,
                                    onValueChange = {
                                        customWeaponRange = it
                                    },
                                    label = {
                                        Text("Range")
                                    },
                                    modifier = Modifier.width(textFieldSize)
                                )
                                var dropdownExpanded by remember { mutableStateOf(false) }
                                var selectedProficiency by remember { mutableStateOf(0) }
                                val proficiencyTypes = listOf(
                                    "Martial" to "Martial weapons",
                                    "Simple" to "Simple weapons",
                                    "Firearm" to "Firearms"
                                )
                                Card(
                                    elevation = 0.dp,
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                                    ),
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.width(textFieldSize)
                                ) {
                                    Text(
                                        text = proficiencyTypes[selectedProficiency].first,
                                        modifier = Modifier
                                            .clickable {
                                                dropdownExpanded = true
                                            }
                                            .padding(
                                                start = 16.dp,
                                                bottom = 16.dp,
                                                end = 16.dp,
                                                top = 19.dp
                                            )
                                    )
                                }
                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }) {
                                    proficiencyTypes.forEachIndexed { i, it ->
                                        DropdownMenuItem(onClick = {
                                            selectedProficiency = i
                                            proficiency =
                                                proficiencyTypes[selectedProficiency].second
                                            dropdownExpanded = false
                                        }) {
                                            Text(it.first)
                                        }
                                    }
                                }
                                //TODO find a way to to properties
                            }
                        }
                    }

                    val enabled = (selected != -1 || (
                            if (customItemName.isBlank()) {
                                false
                            } else {
                                when (customItemType) {
                                    0 -> {
                                        true
                                    }
                                    1 -> {
                                        !(baseAc.isBlank() || maxAcFromDex.isBlank())
                                    }
                                    2 -> {
                                        !(customWeaponDamage.isBlank() || customWeaponRange.isBlank() || customWeaponDamageType.isBlank())
                                    }
                                    else -> {
                                        false
                                    }
                                }
                            }))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                enabled = enabled,
                                onClick = {
                                    if (selected == -1) {
                                        val item: ItemInterface? =
                                            when (customItemType) {
                                                0 -> {
                                                    Item(name = customItemName)
                                                }
                                                1 -> {
                                                    Armor(
                                                        name = customItemName,
                                                        baseAc = baseAc.toInt(),
                                                        dexCap = maxAcFromDex.toInt(),
                                                        stealth = if (stealth) {
                                                            "-"
                                                        } else {
                                                            "Disadvantage"
                                                        }
                                                    )
                                                }
                                                2 -> {
                                                    Weapon(
                                                        name = customItemName,
                                                        damage = customWeaponDamage,
                                                        range = customWeaponRange,
                                                        damageType = customWeaponDamageType,
                                                        proficiency = proficiency
                                                        //TODO properties
                                                    )
                                                }
                                                else -> {
                                                    null
                                                }
                                            }
                                        item?.let { it: ItemInterface ->
                                            onAdd(it)
                                        }
                                    } else {
                                        onAdd(
                                            allItems[selected]
                                        )
                                        selected = -1
                                    }
                                    onDismissRequest()
                                }
                            ) {
                                Text("Add Item")
                            }

                            if(renderBuyButton) {
                                Spacer(Modifier.width(10.dp))

                                Button(
                                    enabled = if (enabled) {
                                        if (allItems.getOrNull(selected)
                                                ?.hasCost() == true
                                        ) {
                                            canBuy(allItems[selected])
                                        } else {
                                            false
                                        }
                                    } else {
                                        false
                                    },
                                    onClick = {
                                        onBuy(allItems[selected])
                                        selected = -1
                                        onDismissRequest()
                                    }
                                ) {
                                    Text("Buy Item")
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}