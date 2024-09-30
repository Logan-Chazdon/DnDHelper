package ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ui.SpellDetailsView
import ui.newCharacter.utils.getDropDownState
import ui.theme.noActionNeeded
import ui.utils.allNames


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmBackgroundView(
    viewModel: NewCharacterConfirmBackgroundViewModel,
    navController: NavHostController,
) {
    val scrollState = rememberScrollState(0)
    val background = viewModel.background.collectAsState(null)

    if (background.value != null) {
        AutoSave(
            "ConfirmBackgroundView",
            { id ->
                viewModel.setBackGround()
                id.value = viewModel.id
            },
            navController
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = background.value?.name ?: "", style = MaterialTheme.typography.h4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                GlobalScope.launch {
                                    viewModel.setBackGround()
                                }
                                navController.navigate("newCharacterView/StatsView/${viewModel.id}")
                            }
                        ) {
                            Text(text = "Set")
                        }

                    }
                }
                Text(
                    text = background.value?.desc ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if(background.value?.equipment?.isEmpty() == false) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.noActionNeeded,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(start = 5.dp)) {
                                Text(
                                    text = "Equipment",
                                    style = MaterialTheme.typography.h6
                                )
                                Text(background.value?.equipment?.let { items ->
                                    var result = ""
                                    items.forEachIndexed { i, item ->
                                        result += item.displayName
                                        if (i != items.size - 1) {
                                            result += ", "
                                        }
                                    }
                                    result.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase() else it.toString()
                                    }
                                } ?: "")
                            }
                        }
                    }

                    background.value?.equipmentChoices?.forEach { choice ->
                        Card(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            backgroundColor = MaterialTheme.colors.surface,
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 5.dp)
                            ) {
                                Text(
                                    text = choice.name,
                                    style = MaterialTheme.typography.h6
                                )

                                //Tell the state bundle what the user can choose from.
                                val names = mutableListOf<String>()
                                for (item in choice.from) {
                                    item.allNames.let { names.add(it) }
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

                    if (background.value?.proficiencies?.isEmpty() == false) {
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
                                    text = "Proficiencies",
                                    style = MaterialTheme.typography.h6
                                )
                                var proficiencies = ""
                                background.value!!.proficiencies.forEach {
                                    proficiencies += it.name + " "
                                }
                                Text(text = proficiencies)
                            }
                        }
                    }

                    if (background.value?.languageChoices?.isNotEmpty() == true) {
                        background.value?.languageChoices?.forEach { choice ->
                            Card(
                                modifier = Modifier.fillMaxWidth(0.95f),
                                backgroundColor = MaterialTheme.colors.surface,
                                elevation = 5.dp,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 5.dp)
                                ) {
                                    Text(
                                        text = choice.name,
                                        style = MaterialTheme.typography.h6,
                                    )

                                    //Tell the state bundle what the user can choose from.
                                    val names = mutableListOf<String>()
                                    for (item in choice.from) {
                                        item.name?.let { names.add(it) }
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
                    }


                    background.value?.spells?.let {
                        if(it.isNotEmpty()) {
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
                                            text = it.name,
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
                                                    SpellDetailsView(spell = it)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //TODO update assumptions.
                    background.value?.features?.forEach {
                        FeatureView(
                            feature = it,
                            level = 1,
                            proficiencies = listOf(),
                            assumedFeatures = listOf(),
                            character = viewModel.character.collectAsState().value,
                            dropDownStates = viewModel.featureDropDownStates,
                            assumedClass = null,
                            assumedSpells = listOf(),
                            assumedStatBonuses = null
                        )
                    }
                }
            }

        }
    }
}