package ui.homebrew

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.shared.generated.resources.Res
import gmail.loganchazdon.dndhelper.shared.generated.resources.ic_class_icon___ranger
import gmail.loganchazdon.dndhelper.shared.generated.resources.ic_race_icon
import kotlinx.coroutines.launch
import model.Race
import org.jetbrains.compose.resources.painterResource
import ui.MultipleFABView

@Composable
fun HomebrewView(navController: NavController, viewModel: HomebrewViewModel) {
    val scope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            MultipleFABView(
                content = {
                    Icon(Icons.Default.Add, "New homebrew")
                },
                items = listOf(
                    {
                        FloatingActionButton(onClick = { navController.navigate("homebrewView/homebrewClassView/-1") }) {
                            Icon(
                                painterResource(Res.drawable.ic_class_icon___ranger),
                                "New class",
                                it
                            ) //Todo make a class icon
                        }
                    },
                    {
                        FloatingActionButton(onClick = { navController.navigate("homebrewView/homebrewRaceView/-1") }) {
                            Icon(
                                painterResource(Res.drawable.ic_race_icon),
                                "New race",
                                it
                            )
                        }
                    }
                )
            )
        }
    ) { paddingValues ->
        var search by remember {
            mutableStateOf("")
        }
        val showRaces = remember {
            mutableStateOf(true)
        }
        val showClasses = remember {
            mutableStateOf(true)
        }
        val showBackgrounds = remember {
            mutableStateOf(true)
        }
        val showSpells = remember {
            mutableStateOf(true)
        }
        val showSubraces = remember {
            mutableStateOf(true)
        }
        val showSubclasses = remember {
            mutableStateOf(true)
        }
        val races = viewModel.races.collectAsState(listOf())
        val classes = viewModel.classes.collectAsState(listOf())
        val spells = viewModel.spells.collectAsState(listOf())
        val subraces = viewModel.subraces.collectAsState(listOf())
        val subclasses = viewModel.subclasses.collectAsState(listOf())
        val backgrounds = viewModel.backgrounds.collectAsState(listOf())
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.95f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Search and filters
                item {
                    var expanded by remember { mutableStateOf(false) }
                    Card(modifier = Modifier.padding(top = 2.dp)) {
                        Column {
                            //Search field
                            TextField(
                                value = search,
                                label = {
                                    Text("Search")
                                },
                                onValueChange = {
                                    search = it
                                },
                                leadingIcon = {
                                    val rotationDegrees by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        if (expanded) { "collapse filters" } else { "expand filters" },
                                        Modifier
                                            .clickable { expanded = !expanded }
                                            .rotate(rotationDegrees)
                                            .size(36.dp)
                                    )
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

                            AnimatedVisibility(
                                visible = expanded,
                                enter = slideInVertically(initialOffsetY = { it / 2  }, animationSpec = tween(durationMillis = 100)) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(durationMillis = 100)) + fadeOut()
                            ) {
                                //Filters
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    FilterItem(
                                        "classes",
                                        showClasses,
                                        onClick = { navController.navigate("homebrewView/homebrewClassView/-1") }
                                    )


                                    FilterItem(
                                        "subclasses",
                                        showSubclasses,
                                        onClick = {
                                            var id = 0
                                            scope.launch(/*Dispatchers.IO*/){
                                                id = viewModel.createDefaultSubclass()
                                            }.invokeOnCompletion {
                                                navController.navigate("homebrewView/homebrewSubclassView/$id")
                                            }
                                        }
                                    )

                                    FilterItem(
                                        "races",
                                        showRaces,
                                        onClick = { navController.navigate("homebrewView/homebrewRaceView/-1") }
                                    )

                                    FilterItem(
                                        "subraces",
                                        showSubraces,
                                        onClick = {
                                            var id = 0
                                            scope.launch(/*Dispatchers.IO*/){
                                                id = viewModel.createDefaultSubrace()
                                            }.invokeOnCompletion {
                                                navController.navigate("homebrewView/homebrewSubraceView/$id")
                                            }
                                        }
                                    )

                                    FilterItem(
                                        "backgrounds",
                                        showBackgrounds,
                                        onClick = {
                                            var id = 0
                                            scope.launch(/*Dispatchers.IO*/){
                                                id = viewModel.createDefaultBackground()
                                            }.invokeOnCompletion {
                                                navController.navigate("homebrewView/homebrewBackgroundView/$id")
                                            }
                                        }
                                    )

                                    FilterItem(
                                        "spells",
                                        showSpells,
                                        onClick = {
                                            var id = 0
                                            scope.launch(/*Dispatchers.IO*/){
                                                id = viewModel.createDefaultSpell()
                                            }.invokeOnCompletion {
                                                navController.navigate("homebrewView/homebrewSpellView/$id")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                //Races
                if (showRaces.value) {
                    items(races.value?.filter {
                        if (search.isBlank()) true else it.raceName.contains(
                            search
                        )
                    } ?: listOf()) { race: Race ->
                        HomebrewItem(
                            name = race.raceName.ifBlank { "Unnamed race" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewRaceView/${race.raceId}")
                            },
                            onDelete = {
                                viewModel.deleteRace(race.raceId)
                            }
                        )
                    }
                }

                //Classes
                if (showClasses.value) {
                    items(classes.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { clazz: model.ClassEntity ->
                        HomebrewItem(
                            name = clazz.name.ifBlank { "Unnamed class" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewClassView/${clazz.id}")
                            },
                            onDelete = {
                                viewModel.deleteClass(clazz.id)
                            }
                        )
                    }
                }

                //Spells
                if (showSpells.value) {
                    items(spells.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { spell ->
                        HomebrewItem(
                            name = spell.name.ifBlank { "Unnamed spell" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewSpellView/${spell.id}")
                            },
                            onDelete = {
                                viewModel.deleteSpell(spell.id)
                            }
                        )
                    }
                }

                //Subraces
                if (showSubraces.value) {
                    items(subraces.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { subrace ->
                        HomebrewItem(
                            name = subrace.name.ifBlank { "Unnamed subrace" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewSubraceView/${subrace.id}")
                            },
                            onDelete = {
                                viewModel.deleteSubrace(subrace.id)
                            }
                        )
                    }
                }


                //Subclasses
                if (showSubclasses.value) {
                    items(subclasses.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { subclass ->
                        HomebrewItem(
                            name = subclass.name.ifBlank { "Unnamed subclass" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewSubclassView/${subclass.subclassId}")
                            },
                            onDelete = {
                                viewModel.deleteSubclass(subclass.subclassId)
                            }
                        )
                    }
                }

                //Backgrounds
                if (showBackgrounds.value) {
                    items(backgrounds.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { background ->
                        HomebrewItem(
                            name = background.name.ifBlank { "Unnamed background" },
                            onClick = {
                                navController.navigate("homebrewView/homebrewBackgroundView/${background.id}")
                            },
                            onDelete = {
                                viewModel.deleteBackground(background.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterItem(
    name: String,
    checked: MutableState<Boolean>,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
                .clickable { checked.value = !checked.value },
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Checkbox(
                checked = checked.value,
                onCheckedChange = null
            )

            Text(
                text = name,
                style = MaterialTheme.typography.h6
            )
        }

        Button(
            onClick = { onClick?.invoke() }
        ) {
            Text("NEW")
        }
    }
}

@Composable
private fun HomebrewItem(
    name: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5
            )

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    Icons.Default.Delete,
                    "Delete homebrew"
                )
            }
        }
    }
}