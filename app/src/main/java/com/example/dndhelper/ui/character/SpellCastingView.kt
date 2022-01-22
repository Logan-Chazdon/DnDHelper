package com.example.dndhelper.ui.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Character


@ExperimentalFoundationApi
@Composable
fun SpellCastingView(
    character: Character,
    modifier: Modifier = Modifier
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
            character.allSpells.forEach { level ->
                stickyHeader {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f).padding(5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (level.key == 0) {
                            Text("Cantrip")
                        } else {
                            Text(level.key.toString())
                            for (index in 0 until character.spellSlots[level.key - 1].maxAmount()) {
                                Canvas(
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    drawCircle(
                                        color = if( character.spellSlots[level.key - 1].currentAmount >= index) {
                                            Color.White
                                        } else {
                                            Color.Gray
                                        },
                                        center = this.center,
                                        style = Fill
                                    )

                                    drawCircle(
                                        color = Color.Black,
                                        center = this.center,
                                        style = Stroke(0.2f)
                                    )
                                }
                            }
                        }
                    }
                }

                items(level.value.size) { i ->
                    val spell = level.value[i]
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(spell.name, Modifier.width(200.dp))
                            Spacer(Modifier.width(2.dp))

                            if (spell.damage != "-") {
                                Text(spell.damage, Modifier.width(100.dp))
                                Spacer(Modifier.width(2.dp))
                            }

                            Text(spell.castingTime, Modifier.width(100.dp))
                            Spacer(Modifier.width(2.dp))
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}