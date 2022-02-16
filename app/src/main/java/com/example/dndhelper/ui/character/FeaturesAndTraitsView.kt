package com.example.dndhelper.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dndhelper.repository.dataClasses.Feature
import com.example.dndhelper.repository.dataClasses.Infusion
import com.example.dndhelper.repository.dataClasses.ItemInterface

@Composable
fun FeaturesAndTraitsView(
    features: List<Pair<Int, Feature>>,
    modifier: Modifier,
    items: List<ItemInterface>,
    infuse: (Infusion, ItemInterface?) -> Unit,
    disableInfusion: (Infusion) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 5.dp
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            state = listState
        ) {
            items(items = features) { item: Pair<Int, Feature> ->
                val maxActive = item.second.maxActive.num(item.first)
                var expanded by remember { mutableStateOf(false) }
                Text(text = item.second.name, modifier = Modifier.clickable { expanded = true })
                item.second.chosen?.forEach { feature ->
                    var activationExpanded by remember { mutableStateOf(false) }

                    if (activationExpanded) {
                        Dialog(
                            onDismissRequest = { activationExpanded = false }
                        ) {
                            Card {
                                Column {
                                    Text(
                                        text = feature.description,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    if (feature.infusion?.active == true) {
                                        //UI to disable the infusion
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Button({
                                                disableInfusion(feature.infusion)
                                                activationExpanded = false
                                            }) {
                                                Text("Disable infusion")
                                            }
                                        }
                                    } else {
                                        //UI to enable the infusion
                                        val targetItems: List<ItemInterface> =
                                            when (feature.infusion?.type) {
                                                //TODO try to refactor this to be automatic
                                                "Weapon" -> {
                                                    items.partition { it.type == "Weapon" }.first
                                                }
                                                "Armor or Shield" -> {
                                                    items.partition { it.type == "Shield" || it.type == "Armor" }.first
                                                }
                                                "Shield" -> {
                                                    items.partition { it.type == "Shield" }.first
                                                }
                                                "Armor" -> {
                                                    items.partition { it.type == "Armor" }.first
                                                }
                                                else -> {
                                                    listOf()
                                                }
                                            }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            var targetItemIndex by remember { mutableStateOf(-1) }
                                            if (feature.infusion?.type != null) {
                                                var dropDownExpanded by remember {
                                                    mutableStateOf(
                                                        false
                                                    )
                                                }
                                                Card(
                                                    backgroundColor = MaterialTheme.colors.primary,
                                                    modifier = Modifier.clickable {
                                                        dropDownExpanded = true
                                                    }
                                                ) {
                                                    Text(
                                                        text = "Item: ${
                                                            if (targetItemIndex < 0) {
                                                                "None"
                                                            } else {
                                                                targetItems[targetItemIndex].name
                                                            }
                                                        }",
                                                        modifier = Modifier.padding(8.dp),
                                                        style = MaterialTheme.typography.button
                                                    )
                                                }
                                                DropdownMenu(
                                                    expanded = dropDownExpanded,
                                                    onDismissRequest = {
                                                        dropDownExpanded = false
                                                    }) {
                                                    DropdownMenuItem(onClick = {
                                                        targetItemIndex = -1
                                                    }) {
                                                        Text("None")
                                                    }
                                                    targetItems.forEachIndexed { index, it ->
                                                        DropdownMenuItem(onClick = {
                                                            targetItemIndex = index
                                                        }) {
                                                            Text(it.name.toString())
                                                        }
                                                    }
                                                }
                                            }

                                            Button({
                                                feature.infusion?.let {
                                                    it.level = item.first
                                                    infuse(
                                                        it,
                                                        targetItems.elementAtOrNull(targetItemIndex)
                                                    )
                                                }
                                                activationExpanded = false
                                            }) {
                                                Text("Activate infusion")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val color = if (maxActive != 0) {
                        if (feature.infusion?.active == true) {
                            MaterialTheme.colors.onBackground
                        } else {
                            MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                                .compositeOver(MaterialTheme.colors.background)
                        }
                    } else {
                        MaterialTheme.colors.onBackground
                    }

                    Text(
                        text = feature.name,
                        modifier = Modifier.padding(start = 5.dp).run {
                            if (maxActive != 0) {
                                this.clickable { activationExpanded = true }
                            } else {
                                this
                            }
                        },
                        color = color
                    )

                }
                Divider(thickness = (0.5).dp, startIndent = 10.dp)
                if (expanded) {
                    Dialog(
                        onDismissRequest = { expanded = false }
                    ) {
                        Card {
                            Text(text = item.second.description, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}