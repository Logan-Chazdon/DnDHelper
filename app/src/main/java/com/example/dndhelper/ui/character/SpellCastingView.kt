package com.example.dndhelper.ui.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Resource
import com.example.dndhelper.repository.dataClasses.Spell


@ExperimentalFoundationApi
@Composable
fun SpellCastingView(
    character: Character,
    modifier: Modifier = Modifier,
    Cast: (Spell) -> Unit
) {
    val state = rememberLazyListState()
    Card(
        elevation = 5.dp,
        modifier = modifier
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val spellSlotsOffsetForCantrips = mutableListOf(
                Resource("Cantrip", 0, "0", "0")
            )
            spellSlotsOffsetForCantrips.addAll(character.spellSlots)

            spellSlotsOffsetForCantrips.forEachIndexed { slotLevel, slots ->

                val spells = character.allSpells[slotLevel]

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f).padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
                    ) {
                        if (slotLevel == 0) {
                            Text("Cantrip")
                        } else {
                            Text(slots.name)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                            ) {
                                val selected = Color.Gray
                                val onPrimary = MaterialTheme.colors.onSurface
                                val surface = MaterialTheme.colors.surface
                                for (index in 0 until slots.maxAmount()) {
                                    Canvas(
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        drawCircle(
                                            color = if (slots.currentAmount > index) {
                                                surface
                                            } else {
                                                selected
                                            },
                                            center = this.center,
                                            style = Fill
                                        )

                                        drawCircle(
                                            color = onPrimary,
                                            center = this.center,
                                            style = Stroke(2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(spells?.size ?: 0) { i ->
                    val spell = spells!![i]
                    Card(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(spell.name, Modifier.width(100.dp))

                            Text(spell.damage, Modifier.width(100.dp))

                            Text(spell.castingTime, Modifier.width(75.dp))

                            if(spell.level != 0) {
                                Button({
                                    Cast(spell)
                                }) {
                                    Text("CAST")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}