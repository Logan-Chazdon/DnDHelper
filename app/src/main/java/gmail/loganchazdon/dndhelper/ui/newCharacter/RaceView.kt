package gmail.loganchazdon.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RaceView(
    viewModel: NewCharacterRaceViewModel,
    navController: NavController,
    characterId: Int
) {
    viewModel.id = characterId
    val races = viewModel.races.observeAsState()

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
        LazyColumn(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = rememberLazyListState()
        ) {
            races.value?.forEachIndexed { i, race ->
                //TODO upgrade search
                if (search == "" || race.name.lowercase().contains(search.lowercase())) {
                    item {
                        Card(
                            backgroundColor = MaterialTheme.colors.surface,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .clickable {
                                    navController.navigate("newCharacterView/RaceView/ConfirmRaceView/$i/$characterId")
                                },
                            shape = RoundedCornerShape(10.dp),
                            elevation = 10.dp
                        ) {
                            Column(
                                Modifier.padding(start = 5.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(text = race.name, fontSize = 24.sp)
                                    Spacer(Modifier.fillMaxWidth(0.1f))
                                    Text(text = race.size, fontSize = 18.sp)
                                }
                                Column {

                                    Text(
                                        text = "Languages",
                                        style = MaterialTheme.typography.subtitle1
                                    )
                                    Row {
                                        for (language in race.languages) {
                                            Text(
                                                text = language.name.toString(),
                                                modifier = Modifier.padding(start = 5.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }

                                    for (trait in race.traits) {
                                        Text(
                                            text = trait.name,
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = trait.description,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }

                                    Row()
                                    {
                                        Column() {
                                            race.abilityBonuses?.forEach { abilityBonus ->
                                                Text(text = abilityBonus.toString())
                                            }
                                        }
                                        Spacer(modifier = Modifier.fillMaxWidth(0.2f))
                                        Text(text = "Speed: ${race.groundSpeed}")
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