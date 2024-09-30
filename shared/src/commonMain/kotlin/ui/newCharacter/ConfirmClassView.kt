package ui.newCharacter


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import model.Feature
import model.Proficiency
import model.Spell
import model.Subclass
import model.repositories.CharacterRepository.Companion.statNames
import ui.SpellDetailsView
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import ui.newCharacter.utils.getDropDownState
import ui.theme.noActionNeeded
import ui.utils.allNames


@OptIn(DelicateCoroutinesApi::class)
@ExperimentalComposeUiApi
@Composable
fun ConfirmClassView(
    viewModel: NewCharacterConfirmClassViewModel,
    navController: NavController
) {
    val clazz = viewModel.clazz.collectAsState(null)
    val subclasses = viewModel.subclasses.collectAsState(emptyList())

    LaunchedEffect(viewModel.hasBaseClass) {
        viewModel.isBaseClass.value = !viewModel.hasBaseClass.value
    }

    val assumedStatBonuses = produceState(
        mutableMapOf<String, Int>(),
        viewModel.absDropDownStates,
        viewModel.isFeat,
        viewModel.featChoiceDropDownStates
    ) {
        viewModel.calculateAssumedStatBonuses()
    }

    val assumedFeatures = produceState(
        emptyList<Feature>(),
        viewModel.featChoiceDropDownStates,
        viewModel.isFeat,
        viewModel.featureDropdownStates
    ) {
        viewModel.calculateAssumedFeatures()
    }

    val assumedProficiencies = produceState(
        emptyList<Proficiency>(),
        viewModel.featChoiceDropDownStates,
        viewModel.isFeat,
        viewModel.dropDownStates
    ) {
        viewModel.calculateAssumedProficiencies()
    }

    val assumedSpells: State<List<Spell>> = produceState(
        initialValue = emptyList(),
        viewModel.subclassSpells.size,
        viewModel.classSpells.size
    ) {
        launch(/*Dispatchers.IO*/) {
            value = viewModel.calculateAssumedSpells()
        }
    }

    LaunchedEffect(
        viewModel.character.collectAsState().value?.id,
        viewModel.featNames.collectAsState(emptyList()).value
    ) {
        if (viewModel.character.value != null && viewModel.featNames.lastOrNull() != null) {
            viewModel.applyAlreadySelectedChoices()
        }
    }

    AutoSave(
        "ConfirmClassView",
        { id ->
            viewModel.addClassLevels()
            id.value = viewModel.id
        },
        navController,
    )

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
                text = clazz.value?.name ?: "",
                style = MaterialTheme.typography.h4,
            )

            val canAfford = produceState(true) {
                viewModel.canAffordMoreClassLevels(
                    try {
                        viewModel.levels.value.text.toInt()
                    } catch (e: Exception) {
                        0
                    }
                )
            }

            //Add Class Button
            Button(
                enabled = canAfford.value,
                onClick = {
                    GlobalScope.launch {
                        viewModel.addClassLevels()
                    }
                    //Navigate to the next step
                    navController.navigate("newCharacterView/RaceView/${viewModel.id}")
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
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
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
                    enabled = !viewModel.hasBaseClass.value
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
            val subclass = produceState(
                null
            ) {
                launch {
                    (viewModel.getSubclassDropdownState()
                        .getSelected(subclasses.value ?: emptyList())
                            as List<Subclass>)
                        .getOrNull(0)
                }
            }

            //Choices for pactMagic.
            clazz.value?.pactMagic?.let { pactMagic ->
                SpellSelectionView(
                    spells = viewModel.classSpells,
                    pactMagic = pactMagic,
                    level = viewModel.toNumber(viewModel.levels),
                    learnableSpells = viewModel.learnableSpells.collectAsState(emptyList()).value,
                    toggleSpell = { viewModel.toggleClassSpell(it) }
                )
            }

            //Choices for spellCasting.
            clazz.value?.spellCasting?.let { spellCasting ->
                SpellSelectionView(
                    spells = viewModel.classSpells,
                    spellCasting = spellCasting,
                    level = viewModel.toNumber(viewModel.levels),
                    learnableSpells = viewModel.learnableSpells.collectAsState(emptyList()).value,
                    toggleSpell = { viewModel.toggleClassSpell(it) }
                )
            }

            //Update the spells.
            LaunchedEffect(
                viewModel.toNumber(viewModel.levels),
                subclass.value,
                clazz.value?.id
            ) {
                this.launch(/*Dispatchers.IO*/) {
                    viewModel.calcLearnableSpells(
                        viewModel.toNumber(viewModel.levels),
                        subclass.value
                    )
                }
            }

            if (viewModel.isBaseClass.value) {
                clazz.value?.proficiencyChoices?.forEach { choice ->
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
                    clazz.value?.equipmentChoices?.forEach { choice ->
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

                                val costs = mutableListOf<Int>()
                                if (!choice.listsCostOne) {
                                    choice.from.forEachIndexed { index, item ->
                                        costs.add(index, item.size)
                                    }
                                }


                                val multipleChoiceState =
                                    viewModel.dropDownStates.getDropDownState(
                                        key = choice.name,
                                        maxOfSameSelection = choice.maxSame,
                                        maxSelections = choice.choose,
                                        names = names,
                                        choiceName = choice.name,
                                        costs = costs
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
                                text = "${clazz.value?.startingGoldD4s ?: 0}d4 * ${clazz.value?.startingGoldMultiplier ?: 10}",
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
                                            if (viewModel.goldRolled.value.toInt() < viewModel.minGoldRolled.value) {
                                                viewModel.goldRolled.value =
                                                    viewModel.minGoldRolled.toString()
                                            } else if (viewModel.goldRolled.value.toInt() > viewModel.maxGoldRolled.value) {
                                                viewModel.goldRolled.value =
                                                    viewModel.maxGoldRolled.toString()
                                            }
                                            focusManager.clearFocus()
                                        }
                                    )
                                )
                                Text(
                                    text = " * ${clazz.value?.startingGoldMultiplier ?: 10}",
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
                            viewModel.featNames.collectAsState(emptyList()).value?.let { featNames ->
                                viewModel.feats.collectAsState(emptyList()).value?.let { feats ->
                                    FeatView(
                                        level = viewModel.toNumber(viewModel.levels),
                                        key = it,
                                        featNames = featNames as MutableList<String>,
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
                                        names = statNames as MutableList<String>,
                                        choiceName = "Ability Score Improvement",
                                        maxOfSameSelection = 2
                                    )
                            )
                        }
                    }
                }
            }



            for (choice in clazz.value?.levelPath ?: emptyList()) {
                if (viewModel.levels.value.text.isNotBlank()) {
                    if (choice.grantedAtLevel <= viewModel.levels.value.text.toInt()) {
                        FeatureView(
                            feature = choice,
                            level = try {
                                viewModel.levels.value.text.toInt()
                            } catch (e: NumberFormatException) {
                                0
                            },
                            assumedFeatures = assumedFeatures.value,
                            character = viewModel.character.collectAsState().value,
                            proficiencies = assumedProficiencies.value,
                            dropDownStates = viewModel.featureDropdownStates,
                            assumedClass = clazz.value,
                            assumedSpells = assumedSpells.value,
                            assumedStatBonuses = assumedStatBonuses.value
                        )
                    }
                }
            }


            //Subclass
            if (
                (clazz.value?.subclassLevel ?: 1)
                <= try {
                    viewModel.levels.value.text.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            ) {
                produceState(MultipleChoiceDropdownStateImpl()) {
                    viewModel.getSubclassDropdownState()
                }.value.let { state ->
                    if (state.names.size != 0) {
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
                                    state = state
                                )
                            }
                        }


                        clazz.let {
                            (state.getSelected(viewModel.subclasses.collectAsState(emptyList()).value) as List<Subclass>)
                                .getOrNull(0)
                        }?.let { subclass ->
                            subclass.spells?.let {
                                if (it.isNotEmpty()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(0.95f),
                                        backgroundColor = MaterialTheme.colors.noActionNeeded,
                                        elevation = 5.dp,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(5.dp)
                                        ) {
                                            Text(
                                                text = "Spells",
                                                style = MaterialTheme.typography.h6
                                            )
                                            it.forEach {
                                                var expanded by remember { mutableStateOf(false) }
                                                Text(
                                                    text = it.second.name,
                                                    modifier = Modifier.clickable {
                                                        expanded = true
                                                    }
                                                )
                                                if (expanded) {
                                                    Dialog(
                                                        onDismissRequest = {
                                                            expanded = false
                                                        },
                                                        properties = DialogProperties(
                                                            usePlatformDefaultWidth = false,
                                                            dismissOnClickOutside = true
                                                        )
                                                    ) {
                                                        Card {
                                                            SpellDetailsView(spell = it.second)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                            subclass.features?.forEach {
                                if (viewModel.levels.value.text.isNotBlank()) {
                                    if (it.grantedAtLevel <= viewModel.levels.value.text.toInt()) {
                                        FeatureView(
                                            feature = it,
                                            level = try {
                                                viewModel.levels.value.text.toInt()
                                            } catch (e: NumberFormatException) {
                                                0
                                            },
                                            assumedFeatures = assumedFeatures.value,
                                            character = viewModel.character.collectAsState().value,
                                            proficiencies = assumedProficiencies.value,
                                            dropDownStates = viewModel.featureDropdownStates,
                                            assumedClass = clazz.value,
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
                                    learnableSpells = viewModel.learnableSpells.collectAsState(emptyList()).value,
                                    toggleSpell = { viewModel.toggleSubclassSpell(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}