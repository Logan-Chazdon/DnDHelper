package com.example.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dndhelper.ui.utils.Dimensions
import com.example.dndhelper.ui.utils.MediaQuery
import com.example.dndhelper.ui.utils.greaterThan
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun StatsView(viewModel: StatsViewModel) {
    val character = viewModel.character?.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Stats
        val stats = character?.value?.getStats()
        val statNames =
            listOf("Strength", "Dexterity", "Constitution", "Wisdom", "Intelligence", "Charisma")
        val statNamesAbr = listOf("Str", "Dex", "Con", "Int", "Wis", "Cha")


        var gridCells = 3
        MediaQuery(Dimensions.Width greaterThan 600.dp) {
            gridCells = 6
        }
        val proficiencyBoxesExpanded =
            remember { mutableStateListOf<Boolean>(true, false, false, false, false, false) }


        LazyVerticalGrid(
            cells = GridCells.Fixed(gridCells)
        ) {
            items(6) { item ->
                val stat = stats?.get(statNamesAbr[item]) ?: 10
                val mod = (stat - 10) / 2
                StatBoxView(stat = statNames[item], value = stat, mod = mod, onClick = {
                    proficiencyBoxesExpanded.replaceAll { false }
                    proficiencyBoxesExpanded[item] = true
                })
            }
        }



        Row(
            modifier = Modifier
                .height(85.dp)
                .fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //Inspiration
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = Color.White
                    )
                    .clickable {
                        GlobalScope.launch {
                            viewModel.toggleInspiration()
                        }
                    },
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Inspiration", Modifier.padding(5.dp))
                    Checkbox(
                        checked = viewModel.character?.observeAsState()?.value?.inspiration
                            ?: false,
                        onCheckedChange = null,
                        Modifier.size(30.dp)
                    )
                }
            }

            Spacer(Modifier.width(20.dp))

            //Passives
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                viewModel.character?.observeAsState()?.value?.passives?.forEach {
                    PassiveStatView(passive = it.key, value = it.value)
                }
            }

        }

        Spacer(Modifier.height(20.dp))

        viewModel.skills?.value?.let {
            for (item in 0 until 5) {
                if (proficiencyBoxesExpanded[item]) {
                    ProficienciesBoxView(
                        baseStat = statNames[item],
                        baseStatNum = viewModel.character?.observeAsState()
                            ?.value?.getStatMod(statNamesAbr[item]) ?: 0,
                        profBonus = viewModel.character?.observeAsState()?.value?.proficiencyBonus ?: 2,
                        stats = viewModel.checkForProficiencies
                            (it[statNames[item]]!!) ?: mutableMapOf(),
                    modifier = Modifier.fillMaxSize(0.9f)
                    )
                }
            }
        }
    }
}

