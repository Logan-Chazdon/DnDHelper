package ui.character

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
import model.Feat
import model.Feature
import model.Infusion
import model.ItemInterface

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

            items(items = features) { item ->
                FeatureDisplayView(
                    feature = item.second,
                    level = item.first,
                    items = items,
                    infuse = infuse,
                    disableInfusion = disableInfusion
                )
                Divider(thickness = (0.5).dp, startIndent = 10.dp)
            }
        }
    }
}

@Composable
private fun FeatureDisplayView(
    feature: Feature, level: Int,
    items: List<ItemInterface>,
    infuse: (Infusion, ItemInterface?) -> Unit,
    disableInfusion: (Infusion) -> Unit)
{
    val maxActive = feature.maxActive.num(level)
    var expanded by remember { mutableStateOf(false) }
    Text(text = feature.name, modifier = Modifier.clickable { expanded = true })
    feature.allChosen.forEach { subFeature ->
        //If this feature has 1 sub feature. For example in the case of replicate magic item.
        //Just fuse the features and display the data of the sub feature.
        var name = subFeature.name
        val desc: String
        if (subFeature.allChosen.size == 1) {
            name += " - " + subFeature.allChosen[0].name
            desc = subFeature.allChosen[0].description
        } else {
            desc = subFeature.description
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
                                text = "${feature.currentActive}/${feature.maxActive.num(level)}"
                            )
                        }
                        Text(
                            text = desc,
                            modifier = Modifier.padding(4.dp)
                        )
                        if (subFeature.infusion?.active == true) {
                            //UI to disable the infusion
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button({
                                    disableInfusion(subFeature.infusion!!)
                                    activationExpanded = false
                                }) {
                                    Text("Disable infusion")
                                }
                            }
                        } else {
                            //UI to enable the infusion
                            //Create a list of all the ItemInterfaces that meet pass the filter.
                            val targetItems: List<ItemInterface> =items.partition {
                                subFeature.infusion?.targetItemFilter
                                    ?.calculate(it) ?: true
                            }.first

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var targetItemIndex by remember { mutableStateOf(-1) }
                                if (subFeature.infusion?.targetItemFilter != null) {
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
                                    enabled = feature.currentActive < feature.maxActive.num(level),
                                    onClick ={
                                        subFeature.infusion?.let {
                                            it.level = level
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
            if (subFeature.infusion?.active == true) {
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

        subFeature.choices?.forEach { featureChoice ->
            Column(modifier = Modifier.padding(start = 10.dp)) {
                featureChoice.chosen?.forEach {
                    FeatureDisplayView(feature = it, level = level, infuse = infuse, disableInfusion= disableInfusion, items = items)
                }
            }
        }

        if (descExpanded) {
            Dialog(
                onDismissRequest = { descExpanded = false }
            ) {
                Card {
                    Text(text = subFeature.description, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }


    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false }
        ) {
            Card {
                Text(text = feature.description, modifier = Modifier.padding(4.dp))
            }
        }
    }
}