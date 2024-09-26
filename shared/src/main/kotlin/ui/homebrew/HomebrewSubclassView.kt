package ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.newCharacter.AutoSave

@Composable
fun HomebrewSubclassView(
    viewModel: SubclassViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val mainLooper = Looper.getMainLooper()
    val features = viewModel.features.observeAsState()

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
                scope.launch(Dispatchers.IO) {
                    val id = viewModel.createDefaultFeature()
                    Handler(mainLooper).post {
                        navController.navigate("homebrewView/homebrewFeature/$id")
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
                        chosen = viewModel.classes.observeAsState(emptyList()).value.map { it.name },
                        onDelete = {
                            scope.launch {
                                viewModel.removeClass(it)
                            }
                        },
                        onExpanded = { expanded.value = !expanded.value }
                    )

                    GenericSelectionPopupView(
                        items = viewModel.allClasses.observeAsState(emptyList()).value,
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