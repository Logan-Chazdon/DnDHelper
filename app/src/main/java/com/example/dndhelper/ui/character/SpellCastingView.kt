package com.example.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Character


@ExperimentalFoundationApi
@Composable
fun SpellCastingView(
    character: Character,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyListState()
    LazyColumn(
        state = state,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        character.allSpells.forEach { level ->
            stickyHeader {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.End
                ) {
                    if(level.key == 0) {
                        Text("Cantrip")
                    } else {
                        Text(level.key.toString())
                        for(index in 0 until character.spellSlots[level.key -1].maxAmount()) {
                                //TODO update this
                                Box(
                                    modifier = Modifier.size(25.dp)
                                        .background(color = if(
                                            character.spellSlots[level.key - 1].currentAmount >= index
                                        ) {
                                            Color.White
                                        } else {
                                            Color.Gray
                                        })
                                ) {

                                }
                        }
                    }
                }
            }

            items(level.value.size) { i ->
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row {
                        Text(level.value[i].name)
                    }
                }
            }
        }
    }
}