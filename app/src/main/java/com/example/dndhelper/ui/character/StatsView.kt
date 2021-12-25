package com.example.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.dndhelper.ui.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun StatsView(viewModel: StatsViewModel) {
    val character = viewModel.character

    Column() {
        //Stats
        val stats = character?.baseStats
        val statNames =
            listOf("Strength", "Dexterity", "Constitution", "Wisdom", "Intelligence", "Charisma")
        val statNamesAbr = listOf("Str", "Dex", "Con", "Int", "Wis", "Cha")


        var gridCells = 3
        MediaQuery(Dimensions.Width greaterThan 600.dp) {
            gridCells = 6
        }


        LazyVerticalGrid(
            cells = GridCells.Fixed(gridCells),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(6) { item ->
                val stat = stats?.get(statNamesAbr[item]) ?: 10
                val mod = (stat - 10) / 2
                StatBoxView(stat = statNames[item], value = stat, mod = mod)
                //TODO add a popup to show all skills Single click shows one double click shows all
            }
        }



        Row(
            modifier = Modifier
                .mediaQuery(Dimensions.Height lessThan 400.dp, Modifier.fillMaxHeight(0.45f).padding(start = 10.dp))
                .mediaQuery(Dimensions.Height greaterThan 400.dp, Modifier.fillMaxHeight(0.25f).padding(start = 16.dp))
        ) {
            //Inspiration
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = Color.White
                    ),
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Inspiration", Modifier.padding(5.dp))
                    Checkbox(checked = true, onCheckedChange = { }, Modifier.size(30.dp))
                }
            }

            //Passives
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for(x in 0 until 3){
                    PassiveStatView(passive = "Passive Perception", value = 12)
                }
            }

        }
    }
}

