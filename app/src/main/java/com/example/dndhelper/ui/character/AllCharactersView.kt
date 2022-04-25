package com.example.dndhelper.ui.character


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController


@ExperimentalFoundationApi
@Composable
fun AllCharactersView(
    allCharactersViewModel: AllCharactersViewModel,
    navController: NavController
) {
    val allCharacters = allCharactersViewModel.getAllCharacters()?.observeAsState()
    val scrollState = rememberScrollState()


    //Popup window to confirm character deletion
    val characterToDeleteIndex = remember { mutableStateOf(0)}
    val openDialog = remember{ mutableStateOf(false) }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Box(Modifier.background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(10.dp))) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Delete: " +
                                (allCharacters?.value?.getOrNull(characterToDeleteIndex.value)?.name ?: "") + "?",
                        fontSize = 20.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { openDialog.value = false }
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                val id = allCharacters?.value?.get(characterToDeleteIndex.value)?.id
                                if (id != null) {
                                    allCharactersViewModel.deleteCharacterById(id)
                                }
                                openDialog.value = false
                            }
                        ) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("newCharacterView/ClassView/-1")
                }
            ) {
                Icon(Icons.Default.Add, "Add Character")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            allCharacters?.value?.forEachIndexed { i, character ->
                Card(
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .combinedClickable(
                            onClick = { navController.navigate("characterView/MainView/${character.id}") },
                            onLongClick = { navController.navigate("newCharacterView/ClassView/${character.id}") }
                        ),
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = character.name, style = MaterialTheme.typography.h5)
                            Spacer(Modifier.width(5.dp))
                            character.background?.let { background -> Text(text = background.name, style = MaterialTheme.typography.body2) }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate("newCharacterView/ClassView/${character.id}")
                                        }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        "Edit Character",
                                        modifier = Modifier.padding(top = 7.dp, end = 7.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            characterToDeleteIndex.value = i
                                            openDialog.value = true
                                        }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        "Delete Character",
                                        modifier = Modifier.padding(top = 7.dp, end = 7.dp)
                                    )
                                }
                            }
                        }
                        Row() {
                            Text(text = character.race?.name.toString(), style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = character.getFormattedClasses(), style = MaterialTheme.typography.body1)
                        }
                    }
                }
            }
        }
    }
}