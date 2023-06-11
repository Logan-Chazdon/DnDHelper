package gmail.loganchazdon.dndhelper.ui.homebrew

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.R
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.ui.MultipleFABView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomebrewView(navController: NavController, viewModel: HomebrewViewModel) {
    val scope = rememberCoroutineScope()
    val looper= Looper.getMainLooper()
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
                                painterResource(R.drawable.ic_class_icon___ranger),
                                "New class",
                                it
                            ) //Todo make a class icon
                        }
                    },
                    {
                        FloatingActionButton(onClick = { navController.navigate("homebrewView/homebrewRaceView/-1") }) {
                            Icon(
                                painterResource(R.drawable.ic_race_icon),
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
        val races = viewModel.races.observeAsState()
        val classes = viewModel.classes.observeAsState()
        val spells = viewModel.spells.observeAsState()
        val subraces = viewModel.subraces.observeAsState()
        val subclasses = viewModel.subclasses.observeAsState()

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
                                            scope.launch(Dispatchers.IO){
                                                val id = viewModel.createDefaultSubclass()
                                                Handler(looper).post {
                                                    navController.navigate("homebrewView/homebrewSubclassView/$id")
                                                }
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
                                            scope.launch(Dispatchers.IO){
                                                val id = viewModel.createDefaultSubrace()
                                                Handler(looper).post {
                                                    navController.navigate("homebrewView/homebrewSubraceView/$id")
                                                }
                                            }
                                        }
                                    )

                                    FilterItem(
                                        "backgrounds",
                                        showBackgrounds,
                                        onClick = {
                                            scope.launch(Dispatchers.IO){
                                                val id = viewModel.createDefaultBackground()
                                                Handler(looper).post {
                                                    navController.navigate("homebrewView/homebrewBackgroundView/$id")
                                                }
                                            }
                                        }
                                    )

                                    FilterItem(
                                        "spells",
                                        showSpells,
                                        onClick = {
                                            scope.launch(Dispatchers.IO){
                                                val id = viewModel.createDefaultSpell()
                                                Handler(looper).post {
                                                    navController.navigate("homebrewView/homebrewSpellView/$id")
                                                }
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
                    } ?: listOf()) { clazz: gmail.loganchazdon.dndhelper.model.ClassEntity ->
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