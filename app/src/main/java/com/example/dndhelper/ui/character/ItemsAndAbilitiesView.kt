package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Character

@Composable
fun ItemsAndAbilitiesView(
    character: Character,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    Card(
        elevation = 5.dp,
        modifier = modifier
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(character.backpack.allWeapons) {
                Row {
                    //TODO add the to hit modifier
                    Text(text = it.name!!, modifier = Modifier.width(100.dp))
                    Text(text = it.damageDesc, modifier = Modifier.width(150.dp))
                    Text(text = it.range, modifier = Modifier.width(100.dp))
                }
                Divider(thickness = 1.dp)
            }
            items(character.combatFeatures) {
                Row {
                    Text(text = it.name, modifier = Modifier.width(100.dp))
                    //Update this to reflect the correct number of max uses
                    Text(
                        text = "${it.resource?.currentAmount} /  ${
                            it.resource?.maxAmount(
                                character.totalClassLevels
                            )
                        } ", modifier = Modifier.width(100.dp)
                    )
                    //TODO add a button to use the feature
                }
                Divider(thickness = 1.dp)
            }
        }
    }
}