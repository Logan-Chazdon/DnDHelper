package com.example.dndhelper.ui.character


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dndhelper.R


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
            Box(Modifier.background(color = Color.White, shape = RoundedCornerShape(10.dp))) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Delete: " +
                                (allCharacters?.value?.get(characterToDeleteIndex.value)?.name ?: "") + "?",
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


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        allCharacters?.value?.forEachIndexed { i, it ->
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(start = 10.dp)
                    .clickable {
                        navController.navigate("characterView/MainView/$i")
                    },
                elevation = 10.dp
            ) {
                Column() {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = it.name, fontSize = 24.sp)
                        Spacer(Modifier.width(5.dp))
                        it.background?.let { it1 -> Text(text = it1.name, fontSize = 14.sp) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
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
                                    modifier = Modifier.padding(7.dp)
                                )
                            }
                        }
                    }
                    Row() {
                       Spacer(modifier = Modifier.width(10.dp))
                       Text(text = it.race?.name.toString(), fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}