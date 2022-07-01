package gmail.loganchazdon.dndhelper.ui.character

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.utils.getValueInCopper
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        //Display item name
                                        Text(
                                            text = item.displayName,
                                            modifier = if (item.type == "Item") {
                                                Modifier
                                            } else {
                                                Modifier.width(screenWidth / 4)
                                            }
                                        )

                                        //Display data  specific to the time type.
                                        when (item.type) {
                                            "Armor" -> {
                                                Text(
                                                    text = (item as Armor).acDesc
                                                )
                                            }
                                            "Shield" -> {
                                                Text(
                                                    text = (item as Shield).totalAcBonus.toString()
                                                )
                                            }
                                            "Weapon" -> {
                                                Text(
                                                    text = (item as Weapon).damageDesc
                                                )
                                            }
                                        }
                                    }
                                    //Display options  specific to the time type.
                                    when (item.type) {
                                        "Armor" -> {
                                            EquipButton(viewModel, item)
                                        }
                                        "Shield" -> {
                                            EquipButton(viewModel, item)
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
                Modifier.absoluteOffset(y = (-20).dp, x = 0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                var isHidden by remember { mutableStateOf(false) }
                Card(
                    shape = CircleShape,
                    elevation = 5.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .absoluteOffset(x = (-4).dp)
                ) {
                    IconButton(onClick = { isHidden = !isHidden }) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            if (isHidden) {
                                "Show currencies"
                            } else {
                                "Hide currencies"
                            },
                            Modifier.rotate(
                                if (isHidden) {
                                    -90f
                                } else {
                                    90f
                                }
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier.absoluteOffset(
                        x = if (isHidden) {
                            (-50).dp
                        } else {
                            0.dp
                        }
                    )
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
                            var text by remember { mutableStateOf("") }
                            val focusManager = LocalFocusManager.current

                            LaunchedEffect(currencies[it.abbreviatedName]) {
                                text = currencies[it.abbreviatedName]?.amount.toString()
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("${it.abbreviatedName}: ", Modifier.padding(start = 4.dp))
                                BasicTextField(
                                    value = text,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            text = currencies[it.abbreviatedName]?.amount.toString()
                                        }),
                                    singleLine = true,
                                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                                    onValueChange = { string ->
                                        text = string
                                        if (string.isNotEmpty())
                                            GlobalScope.launch {
                                                viewModel.addCurrency(
                                                    it.abbreviatedName,
                                                    string.toInt()
                                                )
                                            }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
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
                                itemsIndexed(allItems?.value ?: listOf()) { i, item ->
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
                                            scope.launch(Dispatchers.IO) {
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
                                                        viewModel.addItem(
                                                            it
                                                        )
                                                    }

                                                } else {
                                                    viewModel.addItem(
                                                        allItems!!.value!!.get(
                                                            selected
                                                        )
                                                    )
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
                                            if (allItems?.value?.getOrNull(selected)
                                                    ?.hasCost() == true
                                            ) {
                                                (allItems.value!![selected].cost!!.getValueInCopper()
                                                        <= viewModel.character!!.observeAsState().value!!.backpack.allCurrency.getValueInCopper())
                                            } else {
                                                false
                                            }
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
                                viewModel.character?.value?.backpack?.allItems!![itemToDeleteIndex].displayName
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


@Composable
fun EquipButton(viewModel: ItemViewModel, item: ItemInterface) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        modifier = Modifier.clickable {
            GlobalScope.launch {
                when (item) {
                    is Shield -> {
                        viewModel.equip(item)
                    }
                    is Armor -> {
                        viewModel.equip(item)
                    }
                }
            }
        }
    ) {
        Text(text = "Equip", modifier = Modifier.padding(4.dp))
    }
}
