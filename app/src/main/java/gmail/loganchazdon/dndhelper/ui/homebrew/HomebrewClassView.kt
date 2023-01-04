package gmail.loganchazdon.dndhelper.ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomebrewClassView(
    viewModel: HomebrewClassViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val mainLooper = Looper.getMainLooper()
    val clazz = viewModel.clazz?.observeAsState()

    AutoSave(
        "homebrewClassView",
        { id ->
            viewModel.saveClass()
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
                            Text(text = "Homebrew class name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h5
                    )
                }

                //Hit die
                item {
                    OutlinedTextField(
                        value = viewModel.hitDie.value,
                        onValueChange = {
                            viewModel.hitDie.value = it
                        },
                        label = {
                            Text("Hit die")
                        },
                        leadingIcon = {
                            Text("1d")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                //Subclasses
                item {
                    Card {
                        Column {
                            Text(
                                text = "Subclasses"
                            )

                            OutlinedTextField(
                                value = viewModel.subclassLevel.value,
                                onValueChange = {
                                    viewModel.subclassLevel.value = it
                                },
                                label = {
                                    Text("Subclass level")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            GenericSelectionView(
                                chosen = viewModel.subclasses?.observeAsState(listOf())?.value.let {
                                    val result = mutableListOf<String>()
                                    it?.forEach {
                                        result.add(it.name)
                                    }
                                    result
                                },
                                onDelete = {
                                    viewModel.deleteSubclass(it)
                                },
                                onExpanded = {
                                    //TODO navigate
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}