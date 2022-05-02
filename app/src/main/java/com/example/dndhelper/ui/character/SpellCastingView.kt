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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Resource
import com.example.dndhelper.repository.dataClasses.Spell


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun SpellCastingView(
    allSpells: Map<Int, List<Pair<Boolean?, Spell>>>,
    spellSlotsOffsetForCantrips: MutableList<Resource>,
    modifier: Modifier = Modifier,
    cast: (Spell) -> Unit,
    useSlot: (Int) -> Unit,
    refundSlot: (Int) -> Unit,
    togglePreparation: (Spell) -> Unit
) {
    val hasAnyPreparableSpells = fun() : Boolean {
        allSpells.forEach { spellLevel ->
            spellLevel.value.forEach { (preparable, _) ->
                if(preparable != null) {
                    return true
                }
            }
        }
        return false
    }.invoke()

    val state = rememberLazyListState()
    Card(
        elevation = 5.dp,
        modifier = modifier
    ) {
        val spellLevelsExpanded = remember {
            mutableStateListOf(true, true, true, true, true, true, true, true, true, true)
        }
        var hideNonPreparedSpells by remember {
            mutableStateOf(false)
        }
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            spellSlotsOffsetForCantrips.forEachIndexed { slotLevel, slots ->
                val spells = allSpells[slotLevel]

                item {
                    if(spells?.isNotEmpty() == true || slotLevel != 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(hasAnyPreparableSpells) {
                                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                                    Checkbox(
                                        checked = hideNonPreparedSpells,
                                        onCheckedChange = {
                                            hideNonPreparedSpells = !hideNonPreparedSpells
                                        }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
                            ) {
                                Row(
                                    modifier = Modifier.clickable {
                                        spellLevelsExpanded[slotLevel] =
                                            !spellLevelsExpanded[slotLevel]
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
                                    horizontalArrangement = Arrangement.spacedBy(
                                        4.dp,
                                        Alignment.End
                                    )
                                ) {
                                    val selected = Color.Gray
                                    val onPrimary = MaterialTheme.colors.onSurface
                                    val surface = MaterialTheme.colors.surface
                                    for (index in (0 until slots.maxAmount()).reversed()) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clickable {
                                                    if (slots.currentAmount > index) {
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
                    }
                }

                if (spellLevelsExpanded[slotLevel]) {
                    items(spells?.size ?: 0) { i ->
                        val spell = spells!![i].second
                        val prepared = spells[i].first
                        if (!hideNonPreparedSpells || prepared != false) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .clickable {
                                        cast(spell)
                                    },
                                elevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    //Preparation
                                    Box(
                                        modifier = Modifier.width(20.dp),
                                    ) {
                                        prepared?.let { isPrepared ->
                                            if (spell.level != 0) {
                                                val preparedColor =
                                                    MaterialTheme.colors.onBackground
                                                val onBackground = MaterialTheme.colors.onBackground
                                                val background = MaterialTheme.colors.background
                                                Canvas(modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        togglePreparation(spell)
                                                    }) {
                                                    drawCircle(
                                                        color = if (isPrepared) {
                                                            preparedColor
                                                        } else {
                                                            background
                                                        },
                                                        center = this.center,
                                                        style = Fill
                                                    )

                                                    drawCircle(
                                                        color = onBackground,
                                                        center = this.center,
                                                        style = Stroke(2f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(spell.name, Modifier.width(120.dp))

                                    Text(spell.damage, Modifier.width(140.dp))

                                    Text(spell.castingTime, Modifier.width(160.dp))
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}