package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProficienciesBoxView(
    baseStat: String,
    baseStatNum: Int,
    profBonus: Int,
    stats: Map<String, Boolean>,
    modifier : Modifier
) {
    Card(
        modifier = modifier,
        elevation = 10.dp
    ) {
        Column {
            Text(text = baseStat, fontSize = 18.sp)
            stats.forEach {
                Row {
                    Checkbox(
                        enabled = false,
                        checked = it.value,
                        onCheckedChange = null
                    )
                    Text(it.key + ": ")
                    Text((baseStatNum + if(it.value) { profBonus} else { 0 }).toString())
                }
            }
        }
    }
}