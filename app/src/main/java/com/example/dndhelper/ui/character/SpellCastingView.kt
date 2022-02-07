package com.example.dndhelper.ui.character

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    allSpells: Map<Int, List<Pair<Boolean?, Spell>>>,
    spellSlotsOffsetForCantrips: MutableList<Resource>,
    modifier: Modifier = Modifier,
    Cast: (Spell) -> Unit,
    useSlot: (Int) -> Unit,
    refundSlot: (Int) -> Unit
) {
    val state = rememberLazyListState()
    Card(
        elevation = 5.dp,
        modifier = modifier
    ) {
        val spellLevelsExpanded = remember {
            mutableStateListOf(true, true, true, true, true, true, true, true, true, true)
        }
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            spellSlotsOffsetForCantrips.forEachIndexed { slotLevel, slots ->

                val spells = allSpells[slotLevel]

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f).padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
                    ) {
                        Row(
                            modifier = Modifier.clickable {
                                spellLevelsExpanded[slotLevel] = !spellLevelsExpanded[slotLevel]
                            }
                        ) {
                            val angle: Float by animateFloatAsState(
                                targetValue = if (spellLevelsExpanded[slotLevel]) 0F else -90F,
                                animationSpec = tween(
                                    durationMillis = 150, // duration
                                    easing = FastOutSlowInEasing
                                )
                            )

                            Text(text = slots.name)
                            Icon(
                                Icons.Default.ArrowDropDown, "Drop down",
                                Modifier.rotate(angle)
                            )

                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                        ) {
                            val selected = Color.Gray
                            val onPrimary = MaterialTheme.colors.onSurface
                            val surface = MaterialTheme.colors.surface
                            for (index in (0 until slots.maxAmount()).reversed()) {
                                Canvas(
                                    modifier = Modifier.size(20.dp).clickable {
                                        if(slots.currentAmount > index) {
                                            useSlot(slotLevel)
                                        } else {
                                            refundSlot(slotLevel)
                                        }
                                    }
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

                if (spellLevelsExpanded[slotLevel]) {
                    items(spells?.size ?: 0) { i ->
                        val spell = spells!![i].second
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            elevation = 2.dp
                        ) {
                            Row {
                                Row(
                                    modifier = Modifier.padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(spell.name, Modifier.width(100.dp))

                                    Text(spell.damage, Modifier.width(100.dp))

                                    Text(spell.castingTime, Modifier.width(75.dp))
                                }
                                if (spell.level != 0) {
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
}