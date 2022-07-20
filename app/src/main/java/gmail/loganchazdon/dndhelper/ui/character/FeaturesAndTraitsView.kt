package gmail.loganchazdon.dndhelper.ui.character

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
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.ItemInterface

@Composable
fun FeaturesAndTraitsView(
    feats : List<Feat>?,
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
            if(!feats.isNullOrEmpty()) {
                items(items = feats) { feat ->
                    var expanded by remember { mutableStateOf(false) }
                    Text(text = feat.name, modifier = Modifier.clickable { expanded = true })
                    feat.features?.forEach { feature ->
                        feature.allChosen.forEach {
                            var descExpanded by remember { mutableStateOf(false) }
                            Text(
                                it.name,
                                Modifier
                                    .padding(start = 5.dp)
                                    .clickable { descExpanded = !descExpanded })

                            if (descExpanded) {
                                Dialog(onDismissRequest = { descExpanded = false }) {
                                    Card {
                                        Text(
                                            text = it.description,
                                            modifier = Modifier.padding(4.dp),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Divider(thickness = (0.5).dp, startIndent = 10.dp)

                    if(expanded) {
                        Dialog(onDismissRequest = { expanded = false }) {
                            Card {
                                Text(
                                    text = feat.desc,
                                    modifier = Modifier.padding(4.dp),
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(5.dp))
                    Divider()
                }
            }

            items(items = features) { item: Pair<Int, Feature> ->
                val maxActive = item.second.maxActive.num(item.first)
                var expanded by remember { mutableStateOf(false) }
                Text(text = item.second.name, modifier = Modifier.clickable { expanded = true })
                item.second.allChosen.forEach { feature ->
                    //If this feature has 1 sub feature. For example in the case of replicate magic item.
                    //Just fuse the features and display the data of the sub feature.
                    var name = feature.name
                    val desc: String
                    if (feature.allChosen.size == 1) {
                        name += " - " + feature.allChosen[0].name
                        desc = feature.allChosen[0].description
                    } else {
                        desc = feature.description
                    }

                    var activationExpanded by remember { mutableStateOf(false) }
                    var descExpanded by remember { mutableStateOf(false) }
                    if (activationExpanded) {
                        Dialog(
                            onDismissRequest = { activationExpanded = false }
                        ) {
                            Card {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.subtitle1,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                        Text(
                                            style = MaterialTheme.typography.subtitle2,
                                            text = "${item.second.currentActive}/${item.second.maxActive.num(item.first)}"
                                        )
                                    }
                                    Text(
                                        text = desc,
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
                                        //Create a list of all the ItemInterfaces that meet pass the filter.
                                        val targetItems: List<ItemInterface> =items.partition {
                                            feature.infusion?.targetItemFilter
                                            ?.calculate(it) ?: true
                                        }.first

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            var targetItemIndex by remember { mutableStateOf(-1) }
                                            if (feature.infusion?.targetItemFilter != null) {
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
                                                                targetItems[targetItemIndex].displayName
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
                                                            Text(it.displayName.toString())
                                                        }
                                                    }
                                                }
                                            }

                                            Button(
                                                enabled = item.second.currentActive < item.second.maxActive.num(item.first),
                                                onClick ={
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
                        text = name,
                        modifier = Modifier.padding(start = 5.dp).run {
                            if (maxActive != 0) {
                                this.clickable { activationExpanded = true }
                            } else {
                                this.clickable { descExpanded = true}
                            }
                        },
                        color = color
                    )

                    if (descExpanded) {
                        Dialog(
                            onDismissRequest = { descExpanded = false }
                        ) {
                            Card {
                                Text(text = feature.description, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }

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