package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CombatListView(
    name: String,
    list: List<String>?,
) {
    Card(
        modifier = Modifier.size(100.dp),
        elevation = 10.dp
    ) {
        Column() {

            Text(name)

            LazyColumn(
                Modifier.padding(4.dp)
            ) {
                items(list?.size ?: 0) { it ->
                    list?.get(it)?.let { item -> Text(text = item) }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {

            }
        }
    }
}