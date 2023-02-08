package gmail.loganchazdon.dndhelper.ui.homebrew


import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.model.Choose
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.Proficiency
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureChoiceIndexCrossRef
import gmail.loganchazdon.dndhelper.ui.SpellDetailsView
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomebrewFeatureView(
    viewModel: HomebrewFeatureViewModel,
    navController: NavController? = null
) {
    val looper = Looper.getMainLooper()
    val scope = rememberCoroutineScope { Dispatchers.IO }

    navController?.let {
        AutoSave(
            "homebrewFeature",
            { id ->
                viewModel.saveFeature()
                id.value = viewModel.id
            },
            it,
            true,
        )
    }
    val spellsIsExpanded = remember {
        mutableStateOf(false)
    }
    val infusionIsExpanded = remember {
        mutableStateOf(false)
    }
    val languagesIsExpanded = remember {
        mutableStateOf(false)
    }

    //Spell selection popup.
    GenericSelectionPopupView(
        isExpanded = spellsIsExpanded,
        onItemClick = {
            if (viewModel.spells.contains(it)) {
                viewModel.spells.remove(it)
            } else {
                viewModel.spells.add(it)
            }
        },
        items = viewModel.allSpells.observeAsState(emptyList()).value,
        detailsView = {
            SpellDetailsView(spell = it)
        },
        getName = {
            it.name
        },
        isSelected = {
            viewModel.spells.contains(it)
        }
    )

    //Infusion selection popup.
    GenericSelectionPopupView(
        isExpanded = infusionIsExpanded,
        onItemClick = {
            if (viewModel.infusions.contains(it)) {
                viewModel.infusions.remove(it)
            } else {
                viewModel.infusions.add(it)
            }
        },
        items = viewModel.allInfusions.observeAsState(emptyList()).value,
        detailsView = null,
        getName = {
            it.name
        },
        isSelected = {
            viewModel.infusions.contains(it)
        }
    )

    //Language selection popup.
    GenericSelectionPopupView(
        isExpanded = languagesIsExpanded,
        onItemClick = {
            if (viewModel.languages.contains(it)) {
                viewModel.languages.remove(it)
            } else {
                viewModel.languages.add(it)
            }
        },
        items = viewModel.allLanguages.observeAsState(emptyList()).value,
        detailsView = null,
        getName = {
            it.name ?: ""
        },
        isSelected = {
            viewModel.languages.contains(it)
        }
    )

    LazyColumn(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        //Feature name
        item {
            Card {
                TextField(
                    value = viewModel.featureName.value,
                    onValueChange = {
                        viewModel.featureName.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.h6,
                    label = { Text("Feature name") }
                )
            }
        }

        //Feature description
        item {
            Card {
                TextField(
                    value = viewModel.featureDesc.value,
                    onValueChange = {
                        viewModel.featureDesc.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.body2,
                    placeholder = { Text("Add a description") }
                )
            }
        }

        //Feature level
        item {
            var isError = false
            Card {
                TextField(
                    value = viewModel.featureLevel.value,
                    onValueChange = {
                        viewModel.featureLevel.value = it
                        isError = viewModel.featureLevel.value.toIntOrNull() == null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    label = {
                        Text("Level")
                    }
                )
            }
        }


        //Effects
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                ) {
                    AttributeView(title = "Grants spells", active = viewModel.grantsSpells) {
                        GenericSelectionView(
                            chosen = viewModel.spells.let {
                                val result = mutableListOf<String>()
                                it.forEach { spell ->
                                    result.add(spell.name)
                                }
                                result
                            },
                            onDelete = {
                                viewModel.spells.removeAt(it)
                            },
                            onExpanded = { spellsIsExpanded.value = !spellsIsExpanded.value }
                        )
                    }

                    AttributeView(
                        title = "Grants infusions",
                        active = viewModel.grantsInfusions
                    ) {
                        GenericSelectionView(
                            chosen = viewModel.infusions.let {
                                val result = mutableListOf<String>()
                                it.forEach { infusion ->
                                    result.add(infusion.name)
                                }
                                result
                            },
                            onDelete = {
                                viewModel.infusions.removeAt(it)
                            },
                            onExpanded = { infusionIsExpanded.value = !infusionIsExpanded.value }
                        )
                    }

                    AttributeView(
                        title = "Replaces armor class",
                        active = viewModel.replacesAc
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val allFields = mapOf(
                                viewModel.baseAc to "base AC",
                                viewModel.dexMax to "dex max",
                                viewModel.conMax to "con max",
                                viewModel.wisMax to "wis max"
                            )

                            allFields.forEach { (field, name) ->
                                OutlinedTextField(
                                    value = field.value,
                                    onValueChange = {
                                        field.value = it
                                    },
                                    modifier = Modifier
                                        .weight(1F)
                                        .padding(start = 5.dp),
                                    label = {
                                        Text(name)
                                    }
                                )
                            }
                        }
                    }

                    AttributeView(
                        title = "Grants armor class bonus",
                        active = viewModel.grantsAcBonus
                    ) {
                        OutlinedTextField(
                            value = viewModel.acBonus.value,
                            onValueChange = {
                                viewModel.acBonus.value = it
                            },
                            label = {
                                Text("AC bonus")
                            }
                        )
                    }

                    val featureChoices = viewModel.featureChoices.observeAsState(listOf()).value
                    AttributeView(
                        title = "Contains choices",
                        active = viewModel.containsChoices
                    ) {
                        Column {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                                    .fillMaxWidth()
                                    .heightIn(min = 0.dp, max = 300.dp),
                                state = rememberLazyListState()
                            ) {
                                items(featureChoices) { choice ->
                                    FeatureChoiceView(
                                        choice,
                                        onRemove = { id -> scope.launch { viewModel.removeFeatureChoice(id) } },
                                        onNew = {
                                            scope.launch {
                                                val id = viewModel.createDefaultFeature(choice.id)
                                                Handler(looper).post {
                                                    navController?.navigate("homebrewView/homebrewFeature/${id}")
                                                }
                                            }
                                        },
                                        onNavigate = { id ->
                                            navController?.navigate("homebrewView/homebrewFeature/${id}")
                                        },
                                        onDelete = { id ->
                                            scope.launch {
                                                viewModel.removeFeatureFromChoice(
                                                    featureId = id,
                                                    choiceId = choice.id
                                                )
                                            }
                                        },
                                        getOptions = { id ->
                                            return@FeatureChoiceView viewModel.getOptions(id)
                                        },
                                        indexes = viewModel.featureIndexes.observeAsState(listOf()).value,
                                        selectedIndexes = viewModel.selectedIndexes,
                                        updateChoice = { id, choose ->
                                            scope.launch {
                                                viewModel.updateChoice(id, choose)
                                            }
                                        }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = {
                                    scope.launch {
                                        viewModel.createDefaultFeatureChoice()
                                    }
                                }) {
                                    Text("ADD")
                                }
                            }
                        }
                    }

                    AttributeView(
                        title = "Grants languages",
                        active = viewModel.grantsLanguages
                    ) {
                        GenericSelectionView(
                            chosen = viewModel.languages.let {
                                val result = mutableListOf<String>()
                                it.forEach { language ->
                                    result.add(language.name.toString())
                                }
                                result
                            },
                            onDelete = {
                                viewModel.languages.removeAt(it)
                            },
                            onExpanded = { languagesIsExpanded.value = !languagesIsExpanded.value }
                        )
                    }

                    AttributeView(
                        title = "Grants proficiencies",
                        active = viewModel.grantsProficiencies
                    ) {
                        ProficiencySelectionView(
                            viewModel.proficiencies,
                            viewModel.allProficiencies
                        )
                    }

                    AttributeView(
                        title = "Grants expertise",
                        active = viewModel.grantsExpertise
                    ) {
                        ProficiencySelectionView(
                            viewModel.expertises,
                            viewModel.allProficiencies
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FeatureChoiceView(
    choice: FeatureChoiceEntity,
    onDelete: (Int) -> Unit,
    onNavigate : (Int) -> Unit,
    onNew: () -> Unit,
    onRemove: (Int) -> Unit,
    getOptions: (Int) -> List<Feature>,
    indexes: List<String>,
    selectedIndexes: SnapshotStateList<FeatureChoiceIndexCrossRef>,
    updateChoice: (Int, Choose) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val choose = remember {
        mutableStateOf(choice.choose.num(1).toString())
    }
    val expanded = remember {
        mutableStateOf(false)
    }
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            OutlinedTextField(
                value = choose.value,
                modifier = Modifier.fillMaxWidth(0.5f),
                onValueChange = {
                    choose.value = it
                },
                label = { Text("Choose") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        try {
                            updateChoice(choice.id, Choose(static = choose.value.toInt()))
                        } catch (_: java.lang.NumberFormatException) {
                        }
                        focusManager.clearFocus()
                    }
                )
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                IconButton(
                    onClick = {
                        onRemove(choice.id)
                    }
                ) {
                    Icon(Icons.Default.Delete, "Remove")
                }
            }
        }

        Column {
            val options =
                produceState(initialValue = emptyList<Feature>()) {
                    launch(Dispatchers.IO) {
                        value = getOptions(choice.id)
                    }
                }
            options.value.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        it.name.ifBlank { "Unnamed feature" },
                        modifier = Modifier.clickable {
                            onNavigate(it.featureId)
                        }
                    )
                    IconButton(onClick = { onDelete(it.featureId) }) {
                        Icon(Icons.Default.Delete, "Delete feature")
                    }
                }
            }

            selectedIndexes.forEach {
                IndexView(it, onDelete = { selectedIndexes.remove(it) })
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            Button(onClick = {
                expanded.value = !expanded.value
            }) {
                Text("ADD")
            }
            Button(onClick = onNew) {
                Text("NEW")
            }
        }

        if(expanded.value) {
            Dialog(
                onDismissRequest = { expanded.value = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                )
            ) {
                Card {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        items(indexes) { text ->
                            Card(
                                backgroundColor = if (selectedIndexes.firstOrNull { it.index == text } != null) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.surface
                                },
                                shape = RectangleShape
                            ) {
                                Text(
                                    text = text.replace("_", " "),
                                    modifier = Modifier.fillMaxWidth().padding(5.dp).clickable {
                                        val index = selectedIndexes.indexOfFirst { it.index == text }
                                        if ( index == -1) {
                                            selectedIndexes.add(
                                                FeatureChoiceIndexCrossRef(
                                                    choiceId = choice.id,
                                                    index = text
                                                )
                                            )
                                        } else {
                                            selectedIndexes.removeAt(index)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IndexView(ref: FeatureChoiceIndexCrossRef, onDelete : () -> Unit) {
    val focusManager = LocalFocusManager.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ref.index.replace("_", " "),
                style = MaterialTheme.typography.h6
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "Delete"
                )
            }
        }

        if(ref.index == "Spells") {
            val minSpellLevel = remember { mutableStateOf((ref.levels?.first() ?: 0).toString()) }
            val maxSpellLevel = remember { mutableStateOf((ref.levels?.last() ?: 0).toString()) }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val reCalcSpellLevels = fun() {
                    val min = try {
                        minSpellLevel.value.toInt()
                    } catch(_: java.lang.NumberFormatException) {
                        ref.levels?.first() ?: 0
                    }
                    val max = try {
                        maxSpellLevel.value.toInt()
                    } catch(_: java.lang.NumberFormatException) {
                        ref.levels?.last() ?: 9
                    }
                    val temp = mutableListOf<Int>()
                    for(index in min..max) {
                       temp.add(index)
                    }
                    ref.levels = temp
                }

                OutlinedTextField(
                    value = minSpellLevel.value,
                    onValueChange = {
                        minSpellLevel.value = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        reCalcSpellLevels()
                    },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Minimum spell level")
                    }
                )

                OutlinedTextField(
                    value = maxSpellLevel.value,
                    onValueChange = {
                        maxSpellLevel.value = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        reCalcSpellLevels()
                    },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Maximum spell level")
                    }
                )
            }
        }
    }
}

@Composable
private fun ProficiencySelectionView(
    chosenList: SnapshotStateList<Proficiency>,
    fromList: MutableLiveData<Map<String, List<String>>>,
) {
    val expanded = remember {
        mutableStateOf(false)
    }
    GenericSelectionView(
        chosen = chosenList.let {
            val result = mutableListOf<String>()
            it.forEach { item ->
                result.add(item.name.toString())
            }
            result
        },
        onDelete = {
            chosenList.removeAt(it)
        },
        onExpanded = { expanded.value = !expanded.value }
    )

    GenericSelectionPopupView(
        isExpanded = expanded,
        onItemClick = {
            val index = chosenList.indexOfFirst { prof -> prof.name == it }
            if (index != -1) {
                chosenList.removeAt(index)
            } else {
                chosenList.add(Proficiency(it))
            }
        },
        items = fromList.observeAsState(mapOf()).value.flatMap { it.value },
        detailsView = null,
        getName = {
            it
        },
        isSelected = { s ->
            chosenList.indexOfFirst { prof -> prof.name == s } != -1
        }
    )
}