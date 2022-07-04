package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.model.Subclass
import gmail.loganchazdon.dndhelper.ui.newCharacter.utils.getDropDownState
import gmail.loganchazdon.dndhelper.ui.utils.allNames
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@ExperimentalComposeUiApi
@Composable
fun ConfirmClassView(
    viewModel: NewCharacterClassViewModel,
    navController: NavController
) {
    val classes = viewModel.classes.observeAsState()
    val mainLooper = Looper.getMainLooper()
    LaunchedEffect(viewModel.hasBaseClass) {
        viewModel.isBaseClass.value = !viewModel.hasBaseClass
    }

    val assumedStatBonuses = remember(
        viewModel.absDropDownStates,
        viewModel.isFeat,
        viewModel.featChoiceDropDownStates
    ) {
        derivedStateOf {
            viewModel.calculateAssumedStatBonuses()
        }
    }

    val assumedFeatures = remember (
        viewModel.featChoiceDropDownStates,
        viewModel.isFeat,
        viewModel.featureDropdownStates
    ) {
        derivedStateOf {
            viewModel.calculateAssumedFeatures()
        }
    }

    val assumedProficiencies = remember (
        viewModel.featChoiceDropDownStates,
        viewModel.isFeat,
        viewModel.dropDownStates
    ) {
        derivedStateOf {
            viewModel.calculateAssumedProficiencies()
        }
    }

    val assumedSpells = remember(viewModel.subclassSpells, viewModel.classSpells) {
        derivedStateOf {
            viewModel.calculateAssumedSpells()
        }
    }

    LaunchedEffect(viewModel.character?.value?.id) {
        viewModel.applyAlreadySelectedChoices()
    }

    classes.value?.get(viewModel.classIndex)?.let { clazz ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Text
                Text(
                    text = clazz.name,
                    style = MaterialTheme.typography.h4,
                )


                //Add Class Button
                Button(
                    enabled = viewModel.canAffordMoreClassLevels(
                        try {
                            viewModel.levels.value.text.toInt()
                        } catch (e: java.lang.Exception) {
                            0
                        }
                    ),
                    onClick = {
                        GlobalScope.launch {
                            clazz.let {
                                viewModel.addClassLevels(
                                    it,
                                    viewModel.levels.value.text.toInt()
                                )
                            }
                            //Navigate to the next step
                            Handler(mainLooper).post {
                                navController.navigate("newCharacterView/RaceView/${viewModel.id}")
                            }
                        }
                    },
                ) {
                    Text(text = "Add class")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Level: ", fontSize = 24.sp)
                val focusManager = LocalFocusManager.current

                TextField(
                    value = viewModel.levels.value,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions (
                        onDone = {
                            focusManager.clearFocus()
                        }
                   ),
                    onValueChange = {
                        try {
                            if (it.text.toInt() in 1..20)
                                viewModel.levels.value = it

                        } catch (e: Exception) {
                            if (it.text.isEmpty())
                                viewModel.levels.value = it
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = "Equipment", fontSize = 18.sp)
                    Switch(
                        checked = viewModel.takeGold.value,
                        onCheckedChange = {
                            viewModel.takeGold.value = !viewModel.takeGold.value
                        },
                        enabled = viewModel.isBaseClass.value
                    )
                    Text(text = "Gold", fontSize = 18.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Use as base class", fontSize = 20.sp)
                    Checkbox(
                        checked = viewModel.isBaseClass.value,
                        onCheckedChange = { viewModel.isBaseClass.value = it },
                        enabled = !viewModel.hasBaseClass
                    )
                }
            }

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(state = scrollState, enabled = true),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val subclass = clazz.let {
                    (viewModel.getSubclassDropdownState(
                        it
                    ).getSelected(it.subClasses) as List<Subclass>)
                        .getOrNull(0)
                }

                //Choices for pactMagic.
                clazz.pactMagic?.let { pactMagic ->
                    SpellSelectionView(
                        spells = viewModel.classSpells,
                        pactMagic = pactMagic,
                        level = viewModel.toNumber(viewModel.levels),
                        learnableSpells = viewModel.getLearnableSpells(
                            viewModel.toNumber(viewModel.levels),
                            subclass
                        ),
                        toggleSpell = { viewModel.toggleClassSpell(it) }
                    )
                }

                //Choices for spellCasting.
                clazz.spellCasting?.let { spellCasting ->
                    SpellSelectionView(
                        spells = viewModel.classSpells,
                        spellCasting = spellCasting,
                        level = viewModel.toNumber(viewModel.levels),
                        learnableSpells = viewModel.getLearnableSpells(
                            viewModel.toNumber(viewModel.levels),
                            subclass
                        ),
                        toggleSpell = { viewModel.toggleClassSpell(it) }
                    )
                }


                if (viewModel.isBaseClass.value) {
                    val proficiencyChoices = clazz.proficiencyChoices
                    proficiencyChoices.forEach { choice ->
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
                                Modifier.padding(start = 5.dp)
                            ) {
                                Text(text = choice.name, style = MaterialTheme.typography.h6)

                                //Tell the state bundle what the user can choose from.
                                val names = mutableListOf<String>()
                                for (item in choice.from) {
                                    names.add(item.name.toString())
                                }

                                val multipleChoiceState =
                                    viewModel.dropDownStates.getDropDownState(
                                        key = choice.name,
                                        maxSelections = choice.choose,
                                        names = names,
                                        choiceName = choice.name
                                    )

                                //Create the view.
                                MultipleChoiceDropdownView(state = multipleChoiceState)
                            }
                        }
                    }

                    if (!viewModel.takeGold.value) {
                        val equipmentChoices = clazz.equipmentChoices
                        equipmentChoices.forEach { choice ->
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
                                Column(Modifier.padding(start = 5.dp)) {
                                    Text(
                                        text = choice.name,
                                        style = MaterialTheme.typography.h6
                                    )

                                    //Tell the state bundle what the user can choose from.
                                    val names = mutableListOf<String>()
                                    for (item in choice.from) {
                                        item.allNames.let { names.add(it) }
                                    }


                                    val multipleChoiceState =
                                        viewModel.dropDownStates.getDropDownState(
                                            key = choice.name,
                                            maxSelections = choice.choose,
                                            names = names,
                                            choiceName = choice.name
                                        )

                                    //Create the view.
                                    MultipleChoiceDropdownView(state = multipleChoiceState)
                                }
                            }
                        }
                    } else {
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
                            Column(Modifier.padding(start = 5.dp)) {
                                Text(
                                    text = "Starting gold",
                                    style = MaterialTheme.typography.h6
                                )
                                Text(
                                    text = "${clazz.startingGoldD4s}d4 * ${clazz.startingGoldMultiplier}",
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Row {
                                    val focusManager = LocalFocusManager.current
                                    BasicTextField(
                                        modifier = Modifier.width(IntrinsicSize.Min),
                                        value = viewModel.goldRolled.value,
                                        onValueChange = {
                                            viewModel.goldRolled.value = it
                                        },
                                        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Done,
                                            keyboardType = KeyboardType.NumberPassword
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (viewModel.goldRolled.value.toInt() < viewModel.minGoldRolled) {
                                                    viewModel.goldRolled.value = viewModel.minGoldRolled.toString()
                                                } else if (viewModel.goldRolled.value.toInt() > viewModel.maxGoldRolled) {
                                                    viewModel.goldRolled.value =
                                                        viewModel.maxGoldRolled.toString()
                                                }
                                                focusManager.clearFocus()
                                            }
                                        )
                                    )
                                    Text(
                                        text = " * ${clazz.startingGoldMultiplier}",
                                        style = MaterialTheme.typography.h6
                                    )
                                }
                            }
                        }
                    }
                }


                //ASIs
                for (
                it in 0 until try {
                    viewModel.getAsiNum(viewModel.levels.value.text.toInt())
                } catch (e: NumberFormatException) {
                    0
                }
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(10.dp)
                            ),
                    ) {
                        Column(Modifier.padding(start = 5.dp)) {
                            Text(
                                text = "Feat or Ability score increase",
                                modifier = Modifier.clickable { expanded = !expanded },
                                fontSize = 18.sp
                            )

                            //Fill out the list
                            try {
                                viewModel.isFeat[it]
                            } catch (e: IndexOutOfBoundsException) {
                                viewModel.isFeat.add(it, false)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(onClick = { viewModel.isFeat[it] = true }) {
                                    Text(text = "Feat", fontSize = 18.sp)
                                }
                                DropdownMenuItem(onClick = { viewModel.isFeat[it] = false }) {
                                    Text(text = "Ability Score Increase", fontSize = 18.sp)
                                }
                            }


                            if (viewModel.isFeat[it]) {
                                viewModel.featNames.observeAsState().value?.let { featNames ->
                                    viewModel.feats.observeAsState().value?.let { feats ->
                                        FeatView(
                                            level = viewModel.toNumber(viewModel.levels),
                                            key = it,
                                            featNames = featNames,
                                            featDropDownStates = viewModel.featDropDownStates,
                                            feats = feats,
                                            featChoiceDropDownState = viewModel.featChoiceDropDownStates
                                        )
                                    }
                                }
                            } else {
                                MultipleChoiceDropdownView(
                                    state = viewModel.absDropDownStates
                                        .getDropDownState(
                                            key = it,
                                            maxSelections = 2,
                                            names = viewModel.abilityNames,
                                            choiceName = "Ability Score Improvement",
                                            maxOfSameSelection = 2
                                        )
                                )
                            }
                        }
                    }
                }



                for (choice in clazz.levelPath) {
                    if (viewModel.levels.value.text.isNotBlank()) {
                        if (choice.grantedAtLevel <= viewModel.levels.value.text.toInt()) {
                            FeatureView(
                                feature = choice,
                                level = try {
                                    viewModel.levels.value.text.toInt()
                                } catch (e: java.lang.NumberFormatException) {
                                    0
                                },
                                assumedFeatures = assumedFeatures.value,
                                character = viewModel.character?.observeAsState()?.value,
                                proficiencies = assumedProficiencies.value,
                                dropDownStates = viewModel.featureDropdownStates,
                                assumedClass = viewModel.classes.observeAsState().value?.get(
                                    viewModel.classIndex
                                ),
                                assumedSpells = assumedSpells.value,
                                assumedStatBonuses = assumedStatBonuses.value
                            )
                        }
                    }
                }


                //Subclass
                if (
                    clazz.subclassLevel
                    <= try {
                        viewModel.levels.value.text.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                ) {
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(10.dp)
                            ),
                    ) {
                        Column(Modifier.padding(start = 5.dp)) {
                            Text(text = "Subclass", style = MaterialTheme.typography.h6)
                            MultipleChoiceDropdownView(
                                state = viewModel.getSubclassDropdownState(
                                    clazz
                                )
                            )
                        }
                    }


                    clazz.let {
                        (viewModel.getSubclassDropdownState(
                            it
                        ).getSelected(it.subClasses) as List<Subclass>)
                            .getOrNull(0)
                    }?.let { subclass ->
                        subclass.features.forEach {
                            if (viewModel.levels.value.text.isNotBlank()) {
                                if (it.grantedAtLevel <= viewModel.levels.value.text.toInt()) {
                                    FeatureView(
                                        feature = it,
                                        level = try {
                                            viewModel.levels.value.text.toInt()
                                        } catch (e: java.lang.NumberFormatException) {
                                            0
                                        },
                                        assumedFeatures = assumedFeatures.value,
                                        character = viewModel.character?.observeAsState()?.value,
                                        proficiencies = assumedProficiencies.value,
                                        dropDownStates = viewModel.featureDropdownStates,
                                        assumedClass = viewModel.classes.observeAsState().value?.get(
                                            viewModel.classIndex
                                        ),
                                        assumedSpells = assumedSpells.value,
                                        assumedStatBonuses = assumedStatBonuses.value
                                    )
                                }
                            }
                        }

                        subclass.spellCasting?.let { spellCasting ->
                            SpellSelectionView(
                                spellCasting = spellCasting,
                                spells = viewModel.subclassSpells,
                                level = viewModel.toNumber(viewModel.levels),
                                learnableSpells = viewModel.getLearnableSpells(
                                    subclass,
                                    viewModel.toNumber(viewModel.levels)
                                ),
                                toggleSpell = { viewModel.toggleSubclassSpell(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}