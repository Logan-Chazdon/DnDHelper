package gmail.loganchazdon.dndhelper.ui.homebrew


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.MyApplication
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSourceImpl
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.ui.SpellDetailsView
import gmail.loganchazdon.dndhelper.ui.newCharacter.AutoSave

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HomebrewFeatureView(
    viewModel: HomebrewFeatureViewModel,
    navController: NavController? = null
) {
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

    //Spell selection popup.
    GenericSelectionPopupView(
        isExpanded = spellsIsExpanded,
        onItemClick = {
            if(viewModel.spells.contains(it)) {
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
            if(viewModel.infusions.contains(it)) {
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
                            expanded = spellsIsExpanded
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
                            expanded = infusionIsExpanded
                        )
                    }

                    AttributeView(
                        title = "Replaces armor class",
                        active = viewModel.replacesAc
                    ) {
                        Row {

                        }
                    }

                    AttributeView(
                        title = "Grants armor class bonus",
                        active = viewModel.grantsAcBonus
                    ) {

                    }

                    AttributeView(
                        title = "Contains choices",
                        active = viewModel.containsChoices
                    ) {

                    }

                    AttributeView(
                        title = "Grants languages",
                        active = viewModel.grantsLanguages
                    ) {

                    }

                    AttributeView(
                        title = "Grants proficiencies",
                        active = viewModel.grantsProficiencies
                    ) {

                    }

                    AttributeView(
                        title = "Grants expertise",
                        active = viewModel.grantsExpertise
                    ) {

                    }
                }
            }
        }
    }
}


@Composable
private fun AttributeView(
    title: String,
    active: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2
            )
            Switch(
                checked = active.value,
                onCheckedChange = {
                    active.value = it
                }
            )
        }
        if (active.value) {
            content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(device = "spec:width=411dp,height=891dp,dpi=420", backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewHomebrewFeatureView() {
    HomebrewFeatureView(
        viewModel = HomebrewFeatureViewModel(
            Repository(
                LocalDataSource = LocalDataSourceImpl(context = LocalContext.current),
                dao = null
            ),
            MyApplication(),
            savedStateHandle = SavedStateHandle(),
        )
    )
}