package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = true),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        races.value?.forEachIndexed { i, race ->
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clickable {
                               navController.navigate("newCharacterView/ConfirmRaceView/$i/$characterId")
                    },
                elevation = 10.dp
            ) {
                Column(Modifier.padding(start =5.dp)) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    )
                    {
                        Text(text = race.name, fontSize = 24.sp)
                        Spacer(Modifier.fillMaxWidth(0.1f))
                        Text(text = race.size, fontSize = 18.sp)
                    }
                    Column(
                        modifier = Modifier.padding(start = 20.dp)
                    ) {

                        Text(text = "Languages", fontSize = 16.sp)
                        Row()
                        {
                            for (language in race.languages) {
                                Text(text = language.name.toString(), modifier = Modifier.padding(start = 5.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                        Spacer(Modifier.height(2.dp))

                        for(trait in race.traits) {
                            Text(text = trait.name, fontSize = 18.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(text = trait.description, modifier = Modifier.padding(start = 5.dp))
                        }

                        Spacer(Modifier.height(2.dp))

                        Row()
                        {
                            Column() {
                            for(abilityBonus in race.abilityBonuses) {
                                    Text(text = abilityBonus.toString())
                                }
                            }
                            Spacer(modifier = Modifier.fillMaxWidth(0.2f))
                            Text(text = "Speed: ${race.groundSpeed}")
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}