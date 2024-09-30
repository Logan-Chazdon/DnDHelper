package ui.homebrew


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ui.character.ItemSelectionView
import ui.newCharacter.AutoSave

@Composable
fun HomebrewBackgroundView(
    viewModel : HomebrewBackgroundViewModel,
    navController: NavController
) {
    val background = viewModel.background.collectAsState(null)
    val scope = rememberCoroutineScope()// { Dispatchers.IO }

    LaunchedEffect(background.value?.id) {
        viewModel.apply {
            background.value?.let {
                name.value = it.name
                desc.value = it.desc
                equipment.addAll(it.equipment)
            }
        }
    }

    var itemsExpanded by remember { mutableStateOf(false) }
    if (itemsExpanded) {
        ItemSelectionView(
            allItems = viewModel.allItems.collectAsState(listOf()).value,
            onDismissRequest = { itemsExpanded = false },
            renderBuyButton = false,
            canBuy = { false },
            onBuy = { },
            onAdd = { viewModel.equipment.add(it) }
        )
    }

    AutoSave(
        "homebrewBackgroundView",
        { id ->
            viewModel.saveBackground()
            id.value = viewModel.id
        },
        navController,
        true,
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                var id: Int = 0
                scope.launch {
                    id = viewModel.createDefaultFeature()
                }.invokeOnCompletion {
                    navController.navigate("homebrewView/homebrewFeature/$id")
                }
            }) {
                Icon(Icons.Default.Add, "New Feature")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Card {
                    OutlinedTextField(
                        value = viewModel.name.value,
                        onValueChange = {
                            viewModel.name.value = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.h6,
                        label = { Text("Background name") }
                    )
                }
            }

            item {
                Card {
                    OutlinedTextField(
                        value = viewModel.desc.value,
                        onValueChange = {
                            viewModel.desc.value = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.body2,
                        placeholder = { Text("Add a description") }
                    )
                }
            }

            item {
                Text(text = "Items", style = MaterialTheme.typography.h6)
            }

            item {
                GenericSelectionView(
                    chosen = viewModel.equipment.map { it.name ?: ""} ,
                    onDelete = { viewModel.equipment.removeAt(it) },
                    onExpanded =  { itemsExpanded = true }
                )
            }
        }
    }
}