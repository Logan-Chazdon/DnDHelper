package com.example.dndhelper.ui.character

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndhelper.repository.dataClasses.Armor
import com.example.dndhelper.repository.dataClasses.Item
import com.example.dndhelper.repository.dataClasses.ItemInterface
import com.example.dndhelper.repository.dataClasses.Weapon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ItemsView(viewModel: ItemViewModel) {

    var expanded by remember { mutableStateOf(false) }
    var confirmDeleteExpanded by remember { mutableStateOf(false) }
    var itemToDeleteIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                expanded = !expanded
            }) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.95f),
                shape = RoundedCornerShape(10.dp),
                elevation = 10.dp
            ) {
                val lazyState = rememberLazyListState()
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    state = lazyState
                ) {
                    viewModel.character?.value?.backpack?.allItems.let { items ->
                        items(items?.size ?: 0) { i ->
                            Card(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .padding(top = 2.dp)
                                    .combinedClickable(
                                        onClick = {
                                            //Do nothing
                                            //In the future maybe make this take the user to a detail screen.
                                        },
                                        onLongClick = {
                                            //Ask the user if they want to delete the item
                                            confirmDeleteExpanded = true
                                            itemToDeleteIndex = i
                                        }
                                    )
                            ) {
                                val item = items!![i]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    //Display item name
                                    item.name?.let { string -> Text(text = string) }

                                    //If the item is armor or a weapon display its stats
                                    when (item.type) {
                                        "Armor" -> {
                                            Text(text = (item as Armor).acDesc)
                                            Button(
                                                onClick = {
                                                    GlobalScope.launch {
                                                        viewModel.equip(item)
                                                    }
                                                }
                                            ) {
                                                //TODO maybe add a feature to stop the user from equiping an already equiped item.
                                                Text(text = "Equip")
                                            }
                                        }
                                        "Weapon" -> {
                                            Text(text = (item as Weapon).damageDesc)
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }



        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                Modifier.absoluteOffset(y = (-20).dp, x = 8.dp)
            ) {
                val character = viewModel.character?.observeAsState()
                val currencies = character?.value?.backpack?.allCurrency

                currencies?.forEach { (i, it) ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .width(90.dp),
                        elevation = 5.dp
                    ) {
                        val text = remember { mutableStateOf(it.amount.toString()) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("${it.abbreviatedName}: ", Modifier.padding(start = 4.dp))
                            BasicTextField(
                                value = it.amount.toString(),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                onValueChange = { string ->
                                    text.value = string
                                    if (string.isNotEmpty())
                                        GlobalScope.launch {
                                            viewModel.addCurrency(
                                                it.abbreviatedName,
                                                string.toInt()
                                            )
                                        }
                                    else
                                        GlobalScope.launch {
                                            viewModel.addCurrency(it.abbreviatedName, 0)
                                        }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

            }
        }




        if (expanded) {
            Dialog(
                onDismissRequest = { expanded = false },
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
                        val allItems = viewModel.allItems?.observeAsState()
                        Column {
                            var selected by remember { mutableStateOf(-1) }
                            val scrollState = rememberScrollState()
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

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight(0.55f)
                                    .verticalScroll(scrollState)
                            ) {
                                allItems?.value?.forEachIndexed { i, item ->
                                    //TODO upgrade search
                                    if (
                                        search == "" ||
                                        item.name?.lowercase()?.contains(search.lowercase()) == true
                                    ) {
                                        val color = if (selected == i) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            Color.White
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
                                                Text(text = item.name!!)
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
                                        val textFieldSize = LocalConfiguration.current.screenWidthDp.dp * 0.45f
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
                                val textFieldSize = LocalConfiguration.current.screenWidthDp.dp * 0.45f
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
                                    horizontalArrangement = Arrangement.SpaceEvenly
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
                            } }))
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
                                        scope.launch(Dispatchers.IO) {
                                            if(selected == -1) {
                                                val item : ItemInterface? = when(customItemType) {
                                                    0 -> {
                                                        Item(name = customItemName)
                                                    }
                                                    1 -> {
                                                        Armor(
                                                            name = customItemName,
                                                            baseAc = baseAc.toInt(),
                                                            dexCap = maxAcFromDex.toInt(),
                                                            stealth = if(stealth) { "-" } else { "Disadvantage" }
                                                        )
                                                    }
                                                    2 -> {
                                                        Weapon(
                                                            name = customItemName,
                                                            damage = customWeaponDamage,
                                                            range = customWeaponRange,
                                                            damageType = customWeaponDamageType
                                                            //TODO properties
                                                        )
                                                    }
                                                    else -> {
                                                        null
                                                    }
                                                }
                                                item?.let{ it : ItemInterface -> viewModel.addItem(it) }

                                            } else {
                                                viewModel.addItem(allItems!!.value!!.get(selected))
                                                selected = -1
                                            }
                                            expanded = false
                                        }
                                    }
                                ) {
                                    Text("Add Item")
                                }

                                Spacer(Modifier.width(10.dp))

                                Button(
                                    enabled = if (enabled) {
                                        allItems?.value?.getOrNull(selected)?.hasCost() == true
                                    } else {
                                        false
                                    },
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            viewModel.buyItem(allItems!!.value!!.get(selected))
                                            selected = -1
                                            expanded = false
                                        }
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

    if (confirmDeleteExpanded) {
        AlertDialog(
            onDismissRequest = { confirmDeleteExpanded = false },
            title = {
                Text(text = "Delete Item?")
            },
            text = {
                Text(
                    "Would you like to delete " +
                            "${viewModel.character?.value?.backpack?.allItems!![itemToDeleteIndex].name}"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        GlobalScope.launch {
                            viewModel.deleteItemAt(itemToDeleteIndex)
                        }
                        confirmDeleteExpanded = false
                    }) {
                    Text("Delete Item")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        confirmDeleteExpanded = false
                    }) {
                    Text("Cancel")
                }
            })
    }
}
}