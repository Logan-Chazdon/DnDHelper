package ui.homebrew

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ui.newCharacter.AutoSave

@Composable
fun HomebrewSubclassView(
    viewModel: SubclassViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()// { Dispatchers.IO }
    val features = viewModel.features.collectAsState()

    AutoSave(
        "homebrewSubclassView",
        { id ->
            viewModel.saveSubclass()
            id.value = viewModel.id
        },
        navController,
        true
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                var id = 0
                scope.launch(/*Dispatchers.IO*/) {
                     id = viewModel.createDefaultFeature()
                }.invokeOnCompletion {
                    navController.navigate("homebrewView/homebrewFeature/$id")
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
                            Text(text = "Homebrew subclass name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h5
                    )
                }

                //Features
                features.value?.let {
                    if (it.isNotEmpty()) {
                        item {
                            FeaturesView(
                                features = it,
                                onDelete = { id ->
                                    viewModel.removeFeature(id)
                                },
                                onClick = { id ->
                                    navController.navigate("homebrewView/homebrewFeature/$id")
                                }
                            )
                        }
                    }
                }

                item {
                    Text(text = "Classes", style=  MaterialTheme.typography.h5)
                }

                item {
                    val expanded = mutableStateOf(false)
                    GenericSelectionView(
                        chosen = viewModel.classes.collectAsState(emptyList()).value.map { it.name },
                        onDelete = {
                            scope.launch {
                                viewModel.removeClass(it)
                            }
                        },
                        onExpanded = { expanded.value = !expanded.value }
                    )

                    GenericSelectionPopupView(
                        items = viewModel.allClasses.collectAsState(emptyList()).value,
                        onItemClick = {
                            scope.launch {
                                viewModel.toggleClass(it)
                            }
                        },
                        detailsView = null,
                        isExpanded = expanded,
                        getName = { it.name },
                        isSelected = {
                            viewModel.classes.value?.firstOrNull { item -> item.id == it.id } != null
                        }
                    )
                }
            }
        }
    }
}