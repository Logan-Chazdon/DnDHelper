package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsView(
    viewModel: NewCharacterViewModel
) {
    Column {
        var statGenDropdownExpanded by remember { mutableStateOf(false) }
        val selectedIndexStatGen = viewModel.currentStateGenTypeIndex.observeAsState()
        val statsOptions = viewModel.currentStatsOptions.observeAsState()
        val stats = viewModel.currentStats.observeAsState()
        val statGenOptions = listOf("Point Buy", "Standard Array", "Rolled")
        Text(
            text = statGenOptions[selectedIndexStatGen.value ?: 0],
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { statGenDropdownExpanded = true })
                .background(Color.White),
            fontSize = 20.sp
        )

        val pointsRemaining = viewModel.pointsRemaining.observeAsState()
        if(selectedIndexStatGen.value == 0) {
            Text(
                text = "Points Remaining: ${pointsRemaining.value}"
            )
        }

        DropdownMenu(expanded = statGenDropdownExpanded , onDismissRequest = { statGenDropdownExpanded=false }) {
            statGenOptions.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    viewModel.setCurrentStatGenTypeIndex(index)
                    statGenDropdownExpanded = false
                }) {
                        Text(text = item, fontSize = 20.sp)
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            val selectedIndexes = viewModel.selectedStatIndexes.observeAsState()

            for(i in 0..5) {
                var statChoiceExpanded  by remember { mutableStateOf(false) }
                Text(
                    text = try {stats.value?.get(selectedIndexes.value!![i]).toString()} catch (e : IndexOutOfBoundsException) {"0"},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { statChoiceExpanded = true })
                        .background(Color.White),
                    fontSize = 20.sp
                )

                DropdownMenu(expanded = statChoiceExpanded , onDismissRequest = { statChoiceExpanded=false }) {
                    statsOptions.value?.forEachIndexed { index, item ->
                        DropdownMenuItem(onClick = {
                            viewModel.selectedStatByIndex(i, index)
                            statChoiceExpanded = false
                        }) {
                            Text(text = item.toString(), fontSize = 20.sp)
                        }
                    }
                }

            }
        }

    }
}