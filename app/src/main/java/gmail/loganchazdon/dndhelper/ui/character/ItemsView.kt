package gmail.loganchazdon.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.model.Armor
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.Shield
import gmail.loganchazdon.dndhelper.model.Weapon
import gmail.loganchazdon.dndhelper.model.utils.getValueInCopper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ItemsView(viewModel: ItemViewModel, navController: NavController) {

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
                                            //Take the user to the items detail view
                                            navController.navigate(
                                                "characterView/ItemsView/ItemDetailView/${viewModel.character!!.value!!.id}/$i"
                                            )
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
            ItemSelectionView(
                allItems = viewModel.allItems?.observeAsState()?.value ?: listOf(),
                onAdd = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.addItem(it)
                    }
                },
                onBuy ={
                    scope.launch(Dispatchers.IO) {
                        viewModel.buyItem(it)
                    }
                },
                canBuy = {
                    (it.cost!!.getValueInCopper()
                            <= viewModel.character!!.value!!.backpack.allCurrency.getValueInCopper())
                },
                renderBuyButton = true,
                onDismissRequest = { expanded = false }
            )
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
