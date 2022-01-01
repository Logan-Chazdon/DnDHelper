package com.example.dndhelper.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.dataClasses.ItemInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                    viewModel.character?.value?.backpack?.let { items ->
                        items(items.size) { i ->
                            Card(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier.fillMaxWidth(0.95f)
                            ) {
                                val item = items[i]
                                item.name?.let { name -> Text(text = name) }


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
                val currencies = character?.value?.currency

                currencies?.forEachIndexed { i, it ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .width(90.dp),
                        elevation = 5.dp
                    ) {
                        //TODO this text system is a little bit iffy. Look for a better system later.
                        val text = remember { mutableStateOf(it.amount.toString()) }
                        viewModel.character!!.observeForever() { item ->
                            text.value = item.currency[i].amount.toString()
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("${it.abbreviatedName}: ", Modifier.padding(start = 4.dp))
                            BasicTextField(
                                text.value,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                onValueChange = { string ->
                                    text.value = string
                                    if(string.isNotEmpty())
                                        GlobalScope.launch {
                                            viewModel.addCurrency(it.name, string.toInt())
                                        }
                                }
                            )
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
                    modifier = Modifier.fillMaxSize(0.8f),
                    shape = RoundedCornerShape(10.dp),
                    elevation = 10.dp
                ) {
                    val allItems = viewModel.allItems?.observeAsState()
                    Column {
                        var selected by remember { mutableStateOf(-1) }
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .verticalScroll(scrollState)
                        ) {
                            allItems?.value?.forEachIndexed { i, item ->
                                val color = if(selected == i) {
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
                                        Modifier.padding(5.dp)
                                    ) {
                                        Text(text = item.name!!)
                                    }
                                }
                            }
                        }

                        val enabled = selected != -1
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
                                        GlobalScope.launch {
                                            viewModel.addItem(selected)
                                            selected = -1
                                            expanded = false
                                        }
                                    }
                                ) {
                                    Text("Add Item")
                                }

                                Spacer(Modifier.width(10.dp))

                                Button(
                                    enabled = if(enabled) { allItems?.value?.get(selected)?.cost != null} else { false },
                                    onClick = {
                                        GlobalScope.launch {
                                            viewModel.buyItem(selected)
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
}