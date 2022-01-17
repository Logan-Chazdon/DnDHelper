package com.example.dndhelper.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dndhelper.repository.dataClasses.Feature

@Composable
fun FeaturesAndTraitsView(
    features : List<Feature>,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = 5.dp
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            items(items = features) { item: Feature ->
                var expanded by remember { mutableStateOf(false) }
                Text(text = item.name, modifier = Modifier.clickable { expanded = true })
                item.chosen?.forEach {
                    Text(text = it.name, modifier = Modifier.padding(start = 5.dp))
                }
                if(expanded) {
                    Dialog(
                        onDismissRequest = {expanded = false}
                    ) {
                        Card() {
                            Text(text = item.description, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}