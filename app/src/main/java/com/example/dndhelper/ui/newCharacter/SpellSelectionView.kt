package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dndhelper.repository.dataClasses.PactMagic
import com.example.dndhelper.repository.dataClasses.Spell
import com.example.dndhelper.repository.dataClasses.SpellCasting
import com.example.dndhelper.ui.SpellDetailsView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SpellSelectionView(
    pactMagic: PactMagic,
    spells: MutableList<Spell>,//The spells the user has chosen
    level: Int,
    learnableSpells: List<Spell>, //All the spells the user can choose from
    toggleSpell : (Spell) -> Unit
) {
    SpellSelectionView(
        toggleSpell = toggleSpell,
        pactMagic = pactMagic,
        spellCasting = null,
        spells = spells,
        level = level - 1,//Pre adjusting for array indexing.
        learnableSpells = learnableSpells
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SpellSelectionView(
    spellCasting: SpellCasting,
    spells: MutableList<Spell>, //The spells the user has chosen
    level: Int,
    learnableSpells: List<Spell>,  //All the spells the user can choose from
    toggleSpell : (Spell) -> Unit
) {
    SpellSelectionView(
        toggleSpell = toggleSpell,
        pactMagic = null,
        spellCasting = spellCasting,
        spells = spells,
        level = level - 1, //Pre adjusting for array indexing.
        learnableSpells = learnableSpells
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun SpellSelectionView(
    pactMagic: PactMagic?,
    spellCasting: SpellCasting?,
    spells: MutableList<Spell>,
    level: Int,
    learnableSpells: List<Spell>,
    toggleSpell : (Spell) -> Unit
) {
    //Whether or not the dialog for selecting spells is open.
    var spellsExpanded by remember { mutableStateOf(false) }

    //Try to get all of our values from spellCasting, If its null get them all from pactMagic.
    //There should be no way to call this function with both being null.
    val preparationType = spellCasting?.prepareFrom
    val totalSpells = if(preparationType != "all") {
        spellCasting?.spellsKnown?.getOrNull(level)
            ?: pactMagic!!.spellsKnown[level]
    } else{ 0 }
    val totalCantrips =
        spellCasting?.cantripsKnown?.getOrNull(level)
            ?: pactMagic?.cantripsKnown?.get(level)
            ?: 0

    val castingAbility =
        spellCasting?.castingAbility
            ?: pactMagic!!.castingAbility

    //Function to determine if we can take a certain spell.
    val canTakeSpell = fun (spell: Spell): Boolean {
        val enoughSpellsRemaining =  if(spell.level == 0) {
            //For cantrips.
            spells.count { it.level == 0} < totalCantrips
        } else {
            //For non cantrips.
            if(spellCasting?.hasSpellBook == true) {
                true
            } else {
                spells.count { it.level != 0 } < totalSpells
            }
        }
        if(enoughSpellsRemaining && spellCasting?.schoolRestriction != null && spell.level != 0) {
            return spellCasting.schoolRestriction.isMet(spells.filter { it.level != 0})
                    || spellCasting.schoolRestriction.schools.contains(spell.school)
        }
        return enoughSpellsRemaining
    }

    //The card to render where this is called.
    //Displays the selection button and data.
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(10.dp)
            ),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Text(text = "Spell casting", style = MaterialTheme.typography.h6)
            Text("You may choose $totalSpells spells and $totalCantrips cantrips.")
            spellCasting?.schoolRestriction?.let { schoolRestriction ->
                var schools = ""
                schoolRestriction.schools.forEachIndexed { index, it ->
                    schools += it
                    if (index < schoolRestriction.schools.size - 2) {
                        schools += ", "
                    } else if (index == schoolRestriction.schools.size - 2) {
                        schools += " or "
                    }
                }
                Text("At least ${schoolRestriction.amount} spells must be from $schools.")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Spell mod: $castingAbility")
                if(totalCantrips != 0 || totalSpells != 0) {
                    Button(
                        onClick = { spellsExpanded = true }
                    ) {
                        Text("Choose Spells")
                    }
                }
            }
        }
    }

    //Set in the dialog to choose spells.
    //Used in the spell details dialog.
    var spellDetailsIsExpanded by remember {
        mutableStateOf(false)
    }
    var spellToShowDetailsOf : Spell? by remember {
        mutableStateOf(null)
    }
    //Dialog to choose spells and cantrips.
    if (spellsExpanded) {
        Dialog(
            onDismissRequest = {
                spellsExpanded = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier.fillMaxSize(0.9f),
                shape = RoundedCornerShape(10.dp),
                elevation = 10.dp
            ) {
                Column {
                    var search by remember { mutableStateOf("") }
                    Row(
                        Modifier.fillMaxWidth(),
                    ) {
                        TextField(
                            value = search,
                            label = {
                                Text("Search")
                            },
                            onValueChange = {
                                search = it
                            },
                            singleLine = true,
                            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    "Search"
                                )
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        var lastCategory: Int = -1
                        learnableSpells.forEach { spell ->
                            //TODO upgrade search
                            if (search == "" || spell.name.lowercase()
                                    .contains(search.lowercase())
                            ) {
                                Column {
                                    if (lastCategory != spell.level) {
                                        lastCategory = spell.level
                                        Text(
                                            text = spell.levelName,
                                            style = MaterialTheme.typography.h5
                                        )
                                    }
                                    Card(
                                        shape = RoundedCornerShape(5.dp),
                                        elevation = 2.dp,
                                        modifier = Modifier
                                            .combinedClickable(
                                                onLongClick = {
                                                    spellToShowDetailsOf = spell
                                                    spellDetailsIsExpanded = true
                                                },
                                                onClick = {
                                                    if (canTakeSpell(spell) || spells.contains(spell)) {
                                                        toggleSpell(spell)
                                                    }
                                                }
                                            )
                                            .fillMaxWidth(),
                                        backgroundColor = when {
                                            spells.contains(spell) -> {
                                                MaterialTheme.colors.primary
                                            }
                                            canTakeSpell(spell) -> {
                                                MaterialTheme.colors.background
                                            }
                                            else -> {
                                                MaterialTheme.colors.onBackground.copy(0.5f)
                                                    .compositeOver(MaterialTheme.colors.background)
                                            }
                                        }
                                    ) {
                                        //TODO add more data here
                                        Row(
                                            modifier = Modifier.padding(5.dp)
                                        ) {
                                            Text(
                                                text = spell.name,
                                                modifier = Modifier.width(100.dp)
                                            )
                                            Text(
                                                text = spell.damage,
                                                modifier = Modifier.width(150.dp)
                                            )
                                            Text(
                                                text = spell.range,
                                                modifier = Modifier.width(40.dp)
                                            )
                                            Text(
                                                text = spell.castingTime,
                                                modifier = Modifier.width(90.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Spell Details
    if(spellDetailsIsExpanded){
        Dialog(
            onDismissRequest = {
                spellDetailsIsExpanded = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true
            )
        ) {
            Card {
                spellToShowDetailsOf?.let { SpellDetailsView(spell = it) }
            }
        }
    }
}