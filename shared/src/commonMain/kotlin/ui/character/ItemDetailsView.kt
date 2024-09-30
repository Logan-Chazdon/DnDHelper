package ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import model.Armor
import model.Item
import model.Shield
import model.Weapon

@Composable
fun ItemDetailsView(viewModel: ItemDetailsViewModel) {
    //Observe the item in the viewModel.
    val item =viewModel.item.collectAsState(Item())

    //Update the data displayed to the user when the room db emits and at initial composition.
    LaunchedEffect(item.value?.desc) {
        viewModel.itemDesc.value = item.value?.desc ?: ""
    }

    LaunchedEffect(item.value?.displayName) {
        viewModel.itemName.value = item.value?.displayName ?: ""
    }

    LaunchedEffect(item.value) {
        when (item.value) {
            is Armor -> {
                viewModel.armorBaseAc.value = (item.value as Armor).baseAc.toString()
                viewModel.armorStealthDisadvantage.value =
                    ((item.value as Armor).stealth.lowercase() != "disadvantage")

            }
            is Shield -> {
                viewModel.shieldBaseAc.value = (item.value as Shield).acBonus.toString()
            }
            is Weapon -> {
                viewModel.weaponDamage.value = (item.value as Weapon).damage
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        //Item Name
        TextField(
            value = viewModel.itemName.collectAsState().value,
            onValueChange = { viewModel.itemName.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        //Item Description
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = viewModel.itemDesc.collectAsState().value,
                onValueChange = {
                    viewModel.itemDesc.value = it
                },
                modifier = Modifier.padding(5.dp),
                textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground),
            )

            //If the item has no description prompt them to add one.
            if(viewModel.itemDesc.collectAsState().value == "") {
                Text(
                    text = "Add a description",
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground),
                )
            }
        }

        //Armor Stats
        if(item.value is Armor) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Armor Class
                    Row {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Armor Class:",
                            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                        )

                        BasicTextField(
                            value = viewModel.armorBaseAc.collectAsState().value,
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp)
                                .width(IntrinsicSize.Min),
                            textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                            onValueChange = {
                                viewModel.armorBaseAc.value = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )

                        if((item.value as Armor).dexCap != 0) {
                            Text(
                                text = "+ dex mod",
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp),
                                style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                            )
                            if((item.value as Armor).dexCap < 5) {
                                val maxDex = remember((item.value as Armor).dexCap) {
                                    mutableStateOf((item.value as Armor).dexCap.toString())
                                }
                                Text(
                                    text = "(max",
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                    style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                                )

                                BasicTextField(
                                    value = maxDex.value,
                                    modifier = Modifier
                                        .padding(
                                            top = 5.dp,
                                            bottom = 5.dp,
                                            start = 2.dp,
                                            end = 2.dp
                                        )
                                        .width(IntrinsicSize.Min),
                                    textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                                    onValueChange = {
                                        maxDex.value = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                )

                                Text(
                                    text = ")",
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                    style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                                )
                            }
                        }
                    }

                    //Stealth
                    Row( verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Stealth:",
                            style = MaterialTheme.typography.h6
                        )
                        Checkbox(
                            checked = viewModel.armorStealthDisadvantage.collectAsState().value,
                            onCheckedChange =  {
                                viewModel.armorStealthDisadvantage.value = it
                            }
                        )
                    }
                }
            }
        }

        //Shield Stats
        if(item.value is Shield) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Armor Class:",
                    style = MaterialTheme.typography.h6
                )
                BasicTextField(
                    value = viewModel.shieldBaseAc.collectAsState().value,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp)
                        .width(IntrinsicSize.Min),
                    textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                    onValueChange = {
                        viewModel.shieldBaseAc.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )
            }
        }

        //Weapon Stats
        if(item.value is Weapon) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    BasicTextField(
                        value = viewModel.weaponDamage.collectAsState().value,
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp)
                            .width(IntrinsicSize.Min),
                        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                        onValueChange = {
                            viewModel.weaponDamage.value = it
                        },
                    )

                    val infusionBonus = (item.value as Weapon).getInfusionBonus()
                    val damageType = (item.value as Weapon).damageType
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "+ ${if(infusionBonus != 0){infusionBonus.toString()} else {""}} $damageType",
                        style = MaterialTheme.typography.h6
                    )

                    //TODO make editable
                    (item.value as Weapon).range?.let {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = it,
                            style = MaterialTheme.typography.h6
                        )
                    }

                    //TODO make editable
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = (item.value as Weapon).proficiency.split(" ")[0],
                        style = MaterialTheme.typography.h6
                    )
                }
            }

            //Properties
            //TODO make editable
            (item.value as Weapon).properties?.let { properties ->
                if(properties.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            properties.forEach {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(
                                        top = 5.dp,
                                        start = 5.dp,
                                        end = 5.dp
                                    )
                                )

                                it.desc?.let { desc ->
                                    Text(
                                        text = desc,
                                        modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        //Infusions
        if(item.value?.infusions?.isNotEmpty() == true) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                if(item.value!!.maxInfusions != 1) {
                    Text(
                        text = "Max Infusions: ${item.value!!.maxInfusions}",
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.h6
                    )
                }

                Column {
                    item.value!!.infusions!!.forEach {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(
                                top = 5.dp,
                                start = 5.dp,
                                end = 5.dp
                            )
                        )

                        Text(
                            text = it.desc,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}