package gmail.loganchazdon.dndhelper.ui.homebrew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.R
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.ui.MultipleFABView

@Composable
fun HomebrewView(navController: NavController, viewModel: HomebrewViewModel) {
    Scaffold(
        floatingActionButton = {
            MultipleFABView(
                content = {
                    Icon(Icons.Default.Add, "New homebrew")
                },
                items = listOf(
                    {
                        FloatingActionButton(onClick = { /*TODO*/ }) {
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
        val showRaces = remember{
            mutableStateOf(true)
        }
        val showClasses = remember{
            mutableStateOf(true)
        }
        val showBackgrounds = remember{
            mutableStateOf(true)
        }
        val showSpells = remember{
            mutableStateOf(true)
        }
        val races = viewModel.races.observeAsState()

        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                //Search and filters
                item {
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

                            //Filters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                FilterItem(
                                    "classes",
                                    showClasses
                                )

                                FilterItem(
                                    "races",
                                    showRaces
                                )

                                FilterItem(
                                    "backgrounds",
                                    showBackgrounds
                                )

                                FilterItem(
                                    "spells",
                                    showSpells
                                )
                            }
                        }
                    }
                }

                //Races
                if(showRaces.value) {
                    items(races.value?.filter {
                        if (search.isBlank()) true else it.name.contains(
                            search
                        )
                    } ?: listOf()) { race: Race ->
                        HomebrewItem(
                            name = race.name,
                            onClick = {
                                navController.navigate("homebrewView/homebrewRaceView/${race.id}")
                            },
                            onDelete = {
                                viewModel.deleteRace(race.id)
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
    checked: MutableState<Boolean>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp).clickable { checked.value = !checked.value },
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.h6
        )
        Checkbox(
            checked = checked.value,
            onCheckedChange = null
        )
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