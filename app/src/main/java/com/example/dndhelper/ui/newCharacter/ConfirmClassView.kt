package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.dndhelper.ui.newCharacter.utils.getDropDownState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun ConfirmClassView(viewModel: NewCharacterClassViewModel, navController: NavController, classIndex: Int) {
    viewModel.classIndex = classIndex
    val classes = viewModel.classes.observeAsState()
    val mainLooper = Looper.getMainLooper()
    var spellsExpanded by remember { mutableStateOf(false) }
    val levels = remember {
        mutableStateOf(TextFieldValue("1"))
    }
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
            classes.value?.get(classIndex)?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.h4,
                )
            }

            //Add Class Button
            Button(
                enabled = viewModel.canAffordMoreClassLevels(
                    try {
                        levels.value.text.toInt()
                    } catch (e: java.lang.Exception) {
                        0
                    }
                ),
                onClick = {
                    GlobalScope.launch {
                        classes.value?.get(classIndex)
                            ?.let { viewModel.addClassLevels(it, levels.value.text.toInt()) }
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
        )
        {
            Text(text = "Level: ", fontSize = 24.sp)

            TextField(
                value = levels.value,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = {
                    try {
                        if (it.text.toInt() in 1..20)
                            levels.value = it

                    } catch (e: Exception) {
                        if (it.text.isEmpty())
                            levels.value = it
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
                    onCheckedChange = { viewModel.takeGold.value = !viewModel.takeGold.value },
                    enabled = viewModel.isBaseClass.value
                )
                Text(text = "Gold", fontSize = 18.sp)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
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
            if (viewModel.learnsSpells(classIndex)) {
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
                        Text(
                            "You may choose "
                                    + viewModel.totalSpells(classIndex, levels)
                                    + " spells and "
                                    + viewModel.totalCantrips(classIndex, levels)
                                    + " cantrips."
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Spell mod: " + viewModel.getCastingMod(classIndex))

                            Button(
                                onClick = { spellsExpanded = true }
                            ) {
                                Text("Choose Spells")
                            }
                        }
                    }
                }
            }

            if (viewModel.isBaseClass.value) {
                val proficiencyChoices =
                    viewModel.classes.observeAsState().value?.get(classIndex)?.proficiencyChoices
                proficiencyChoices?.forEach { choice ->
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
                                item.name?.let { names.add(it) }
                            }

                            val multipleChoiceState = viewModel.dropDownStates.getDropDownState(
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

                if(!viewModel.takeGold.value) {
                    val equipmentChoices =
                        viewModel.classes.observeAsState().value?.get(classIndex)?.equipmentChoices
                    equipmentChoices?.forEach { choice ->
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
                                Text(text = choice.name, style = MaterialTheme.typography.h6)

                                //Tell the state bundle what the user can choose from.
                                val names = mutableListOf<String>()
                                for (item in choice.from) {
                                    item.name?.let { names.add(it) }
                                }


                                val multipleChoiceState = viewModel.dropDownStates.getDropDownState(
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
                            //TODO add a starting gold card here.
                        }
                    }
                }
            }

            //Subclass
            if (
                viewModel.classes.observeAsState().value?.get(classIndex)?.subclassLevel ?: 21
                <= try {
                    levels.value.text.toInt()
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
                                viewModel.classes.observeAsState().value?.get(
                                    classIndex
                                )!!
                            )
                        )
                    }
                }
            }

            //ASIs
            for (
            it in 0 until try {
                viewModel.getAsiNum(levels.value.text.toInt())
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

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = { viewModel.isFeat[it] = true }) {
                                Text(text = "Feat", fontSize = 18.sp)
                            }
                            DropdownMenuItem(onClick = { viewModel.isFeat[it] = false }) {
                                Text(text = "Ability Score Increase", fontSize = 18.sp)
                            }
                        }


                        if (viewModel.isFeat[it]) {
                            viewModel.featNames.observeAsState().value?.let { featNames ->
                                viewModel.featDropDownStates
                                    .getDropDownState(
                                        key = it,
                                        maxSelections = 1,
                                        names = featNames,
                                        choiceName = "Feat"
                                    )
                            }?.let { state ->
                                MultipleChoiceDropdownView(
                                    state = state
                                )
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


            val levelPath = viewModel.classes.observeAsState().value?.get(classIndex)?.levelPath
            if (levelPath != null) {
                for (choice in levelPath) {
                    if (levels.value.text.isNotBlank())
                        if (choice.grantedAtLevel <= levels.value.text.toInt()) {
                            val color = if (choice.choose.num(levels.value.text) != 0) {
                                MaterialTheme.colors.surface
                            } else {
                                MaterialTheme.colors.onBackground.copy(alpha = 0.3f)
                                    .compositeOver(MaterialTheme.colors.background)
                            }
                            Card(
                                elevation = 5.dp,
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .background(color = color, shape = RoundedCornerShape(10.dp)),
                                backgroundColor = color
                            ) {
                                Column(Modifier.padding(start = 5.dp)) {
                                    Text(text = choice.name, style = MaterialTheme.typography.h6)
                                    Text(
                                        text = choice.description,
                                        style = MaterialTheme.typography.caption
                                    )



                                    if (choice.choose.num(levels.value.text) != 0) {
                                        val options = choice.getAvailableOptions(
                                            viewModel.character,
                                            viewModel.proficiencies,
                                            levels.value.text
                                        )
                                        MultipleChoiceDropdownView(
                                            state = viewModel.dropDownStates.getDropDownState(
                                                key = choice.name + choice.grantedAtLevel,
                                                choiceName = choice.name,
                                                maxSelections = choice.choose.num(levels.value.text),
                                                names = options.let { list ->
                                                    val result = mutableListOf<String>()
                                                    list.forEach {
                                                        result.add(it.name)
                                                    }
                                                    result
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

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
                        Modifier
                            .fillMaxWidth(),
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
                        viewModel.getSpells(classIndex).let { spells ->
                            var lastCategory: Int = -1
                            spells.forEach {
                                //TODO upgrade search
                                if (search == "" || it.name.lowercase()
                                        .contains(search.lowercase())
                                ) {
                                    Column {
                                        if (lastCategory != it.level) {
                                            lastCategory = it.level
                                            Text(
                                                text = it.levelName,
                                                style = MaterialTheme.typography.h5
                                            )
                                        }
                                        Card(
                                            shape = RoundedCornerShape(5.dp),
                                            elevation = 2.dp,
                                            modifier = Modifier
                                                //TODO long clickable for detail view
                                                .clickable {
                                                    if (
                                                        viewModel.canAffordSpellOfLevel(
                                                            it.level,
                                                            classIndex,
                                                            levels.value.text.toInt()
                                                        )
                                                        || viewModel.spells.contains(it)
                                                    ) {
                                                        viewModel.toggleSpell(it)
                                                    }
                                                }
                                                .fillMaxWidth(),
                                            backgroundColor = when {
                                                viewModel.spells.contains(it) -> {
                                                    MaterialTheme.colors.primary
                                                }
                                                viewModel.canAffordSpellOfLevel(
                                                    it.level,
                                                    classIndex,
                                                    levels.value.text.toInt()
                                                ) -> {
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
                                                    text = it.name,
                                                    modifier = Modifier.width(100.dp)
                                                )
                                                Text(
                                                    text = it.damage,
                                                    modifier = Modifier.width(150.dp)
                                                )
                                                Text(
                                                    text = it.range,
                                                    modifier = Modifier.width(40.dp)
                                                )
                                                Text(
                                                    text = it.castingTime,
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
    }
}



