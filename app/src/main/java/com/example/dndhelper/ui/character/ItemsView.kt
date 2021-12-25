package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.exp

@Composable
fun ItemsView(viewModel : ItemViewModel) {

    var expanded by remember{ mutableStateOf(false)}

    Scaffold(
        floatingActionButton =  {
            FloatingActionButton(onClick = {
                expanded = true
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
                    items(5) {
                        Card(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        ) {
                            Text(text = "Mace")
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
                for (x in 0 until 5) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp).width(90.dp),
                        elevation = 5.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("GP: ", Modifier.padding(start = 4.dp))
                            BasicTextField("20", onValueChange = {

                            })
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }




        if(expanded) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.size(200.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = 10.dp
                ) {
                    Text("yeet")
                }
            }
        }

    }
}