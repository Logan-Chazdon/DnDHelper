package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dndhelper.model.*
import com.example.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import com.example.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import com.example.dndhelper.ui.theme.noActionNeeded
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun ConfirmRaceView(
    viewModel: NewCharacterRaceViewModel,
    navController: NavController,
    raceIndex: Int,
    characterId: Int
) {
    viewModel.raceIndex = raceIndex
    val races = viewModel.races.observeAsState()
    val scrollState = rememberScrollState(0)
    viewModel.id = characterId
    val mainLooper = Looper.getMainLooper()

    val assumedStatBonuses = remember(
        viewModel.subraceASIDropdownState
    ) {
        derivedStateOf {
            viewModel.calculateAssumedStatBonuses()
        }
    }

    val assumedSpells = remember {
        derivedStateOf {
            viewModel.calculateAssumedSpells()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            races.value?.get(raceIndex)?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.h4,
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = it.size,
                    fontSize = 16.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        //Change the race
                        GlobalScope.launch {
                            viewModel.setRace(it)
                            //Navigate to the next step
                            Handler(mainLooper).post {
                                navController.navigate("newCharacterView/BackgroundView/${viewModel.id}")
                            }
                        }

                    }) {
                        Text(text = "Set as race")
                    }
                }
            }
        }

        races.value?.get(raceIndex)
            ?.let { Text(text = it.sizeDesc, Modifier.padding(start = 5.dp, top = 5.dp)) }
        races.value?.get(raceIndex)
            ?.let {
                it.alignment?.let { it1 ->
                    Text(
                        text = it1,
                        Modifier.padding(start = 5.dp, top = 5.dp)
                    )
                }
            }
        races.value?.get(raceIndex)?.let {
            Text(
                text = "Speed: ${it.groundSpeed}",
                Modifier.padding(start = 5.dp, top = 5.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (races.value?.getOrNull(raceIndex)?.subraces?.getOrNull(viewModel.subraceIndex.value)?.languages.isNullOrEmpty() &&
                races.value?.getOrNull(raceIndex)?.subraces?.getOrNull(viewModel.subraceIndex.value)?.languageChoices.isNullOrEmpty()
            )
                RaceLanguagesView(
                    languageDesc = races.value?.get(raceIndex)?.languageDesc,
                    languages = races.value?.get(raceIndex)?.languages,
                    languageChoices = races.value?.get(raceIndex)?.languageChoices,
                    dropDownStates = viewModel.languageDropdownStates
                )

            races.value?.get(raceIndex)?.abilityBonuses?.let { bonuses ->
                RaceAbilityBonusesView(bonuses)
            }

            races.value?.get(raceIndex)?.let { race ->
                RaceFeaturesView(
                    character = viewModel.character.observeAsState().value,
                    features = viewModel.filterRaceFeatures(race),
                    dropDownStates = viewModel.raceFeaturesDropdownStates,
                    proficiencies = viewModel.proficiencies,
                    assumedSpells = assumedSpells.value,
                    assumedStatBonuses = assumedStatBonuses.value
                )

                if (!(race.proficiencyChoices.isNullOrEmpty() && race.startingProficiencies.isNullOrEmpty())) {
                    RaceContentCard(title = "Proficiencies", race.proficiencyChoices.size > 0) {
                        race.startingProficiencies.let {
                            var string = ""
                            it.forEachIndexed { index, prof ->
                                string += prof.name
                                if (index != it.size - 1) {
                                    string += ", "
                                }
                            }
                            if (string.isNotEmpty()) {
                                "$string."
                            } else {
                                null
                            }
                        }?.let {
                            Text(it)
                        }

                        race.proficiencyChoices.forEach { proficiencyChoice ->
                            MultipleChoiceDropdownView(
                                state = viewModel.raceProficiencyChoiceDropdownStates.getDropDownState(
                                    key = proficiencyChoice.name,
                                    maxSelections = proficiencyChoice.choose,
                                    names = proficiencyChoice.from.let { proficiencyChoices ->
                                        val names = mutableListOf<String>()
                                        proficiencyChoices.forEach {
                                            it.name?.let { name -> names.add(name) }
                                        }
                                        names
                                    },
                                    maxOfSameSelection = 1,
                                    choiceName = proficiencyChoice.name
                                )
                            )
                        }
                    }
                }
            }



            races.value?.get(raceIndex)?.subraces?.let { subraces ->
                if (races.value?.get(raceIndex)?.subraces?.isNotEmpty() == true) {
                    RaceContentCard("Subrace", true) {
                        subraces[viewModel.subraceIndex.value].let { subrace ->
                            Box {
                                var expanded by remember { mutableStateOf(false) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.clickable { expanded = true }
                                ) {
                                    Text(
                                        text = subrace.name,
                                        style = MaterialTheme.typography.body1,
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        "Drop down"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    races.value?.get(raceIndex)?.subraces!!.forEachIndexed { index, item ->
                                        DropdownMenuItem(onClick = {
                                            viewModel.subraceIndex.value = index
                                            expanded = false
                                        }) {
                                            Text(text = item.name)
                                        }

                                    }
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                subrace.size?.let {
                                    Text(it)
                                }
                                subrace.groundSpeed?.let {
                                    Text("${it}ft")
                                }
                            }
                        }
                    }

                    if (!(subraces[viewModel.subraceIndex.value].languages.isNullOrEmpty() &&
                                subraces[viewModel.subraceIndex.value].languageChoices.isNullOrEmpty())
                    )
                        RaceLanguagesView(
                            languageDesc = null,
                            languages = subraces[viewModel.subraceIndex.value].languages,
                            languageChoices = subraces[viewModel.subraceIndex.value].languageChoices,
                            dropDownStates = viewModel.languageDropdownStates
                        )

                    subraces[viewModel.subraceIndex.value].abilityBonuses?.let { bonuses ->
                        RaceAbilityBonusesView(bonuses)
                    }

                    subraces[viewModel.subraceIndex.value].featChoices?.forEachIndexed { index, it ->
                        RaceContentCard(it.name, containsChoice = true) {
                            FeatView(
                                level = 1,
                                key = index,
                                featNames = it.from.run {
                                    val result = mutableListOf<String>()
                                    this.forEach {
                                        result.add(it.name)
                                    }
                                    result
                                },
                                feats = it.from,
                                featDropDownStates = viewModel.subraceFeatDropdownStates,
                                featChoiceDropDownState = viewModel.subraceFeatChoiceDropDownStates
                            )
                        }
                    }


                    subraces[viewModel.subraceIndex.value].abilityBonusChoice?.let { choice ->
                        RaceContentCard("Ability bonuses", true) {
                            val state = viewModel.subraceASIDropdownState.value.let {
                                if (it == null) {
                                    val newState = MultipleChoiceDropdownStateImpl()
                                    newState.names = choice.from.let { list ->
                                        val names = mutableListOf<String>()
                                        list.forEach {
                                            names.add(it.ability)
                                        }
                                        names
                                    }

                                    newState.maxSameSelections = choice.maxOccurrencesOfAbility
                                    newState.choiceName = "Ability bonuses"
                                    newState.maxSelections = choice.choose
                                    viewModel.subraceASIDropdownState.value = newState
                                    newState
                                } else {
                                    it
                                }
                            }
                            MultipleChoiceDropdownView(state = state)
                        }
                    }

                    RaceFeaturesView(
                        character = viewModel.character.observeAsState().value,
                        features = subraces[viewModel.subraceIndex.value].traits,
                        dropDownStates = viewModel.subraceFeaturesDropdownStates,
                        proficiencies = viewModel.proficiencies,
                        assumedSpells = assumedSpells.value,
                        assumedStatBonuses = assumedStatBonuses.value
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceContentCard(
    title: String,
    containsChoice: Boolean,
    Content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.95f),
        backgroundColor = if (containsChoice) {
            MaterialTheme.colors.surface
        } else {
            MaterialTheme.colors.noActionNeeded
        },
        elevation = 5.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(5.dp)) {
            Text(title, style = MaterialTheme.typography.h6)
            Content()
        }
    }
}

@Composable
private fun RaceFeaturesView(
    character: Character?,
    features: List<Feature>,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>,
    proficiencies: List<Proficiency>,
    assumedSpells: List<Spell>,
    assumedStatBonuses: MutableMap<String, Int>
) {
    features.forEach { feature ->
        FeatureView(
            feature = feature,
            assumedFeatures = listOf(),
            level = character?.totalClassLevels ?: 0,
            proficiencies = proficiencies,
            character = character,
            dropDownStates = dropDownStates,
            assumedClass = null,
            assumedSpells = assumedSpells,
            assumedStatBonuses = assumedStatBonuses
        )
    }
}

@Composable
private fun RaceLanguagesView(
    languageDesc: String?,
    languages: List<Language>?,
    languageChoices: List<LanguageChoice>?,
    dropDownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateImpl>
) {
    RaceContentCard("Languages", (languageChoices?.size ?: 0) > 0) {
        Text(
            languageDesc ?: (languages.let { langs ->
                val names = mutableListOf<String>()
                langs?.forEach {
                    it.name?.let { name -> names.add(name) }
                }
                names
            }.plus(languageChoices?.let {
                val names = mutableListOf<String>()
                it.forEach { lang ->
                    names.add(lang.name)
                }
                names
            } ?: listOf())).let {
                var string = ""
                it.forEachIndexed { index, item ->
                    string += when (index) {
                        it.size - 1 -> {
                            "and $item."
                        }
                        it.size -> {
                            item
                        }
                        else -> {
                            "${item}, "
                        }
                    }
                }
                string
            })
        languageChoices?.let { choice ->
            choice.forEach {
                MultipleChoiceDropdownView(state = dropDownStates.getDropDownState(
                    key = it.name,
                    maxSelections = it.choose,
                    maxOfSameSelection = 1,
                    choiceName = it.name,
                    names = it.from.let { from ->
                        val names = mutableListOf<String>()
                        from.forEach { lang ->
                            names.add(lang.name.toString())
                        }
                        names
                    }
                ))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaceAbilityBonusesView(bonuses: List<AbilityBonus>) {
    if (bonuses.isNotEmpty())
        RaceContentCard("Ability bonuses", false) {
            Column {
                var i = 0
                while(i < bonuses.size) {
                    Row {
                        i += 1
                        Text(
                            text = "+${bonuses[i].bonus} ${bonuses[i].ability}",
                            modifier = Modifier.fillMaxWidth(0.45f)
                        )
                        i += 1
                        if(i < bonuses.size) {
                            Text(
                                text = "+${bonuses[i].bonus} ${bonuses[i].ability}"
                            )
                        }
                    }
                }
            }
        }
}

