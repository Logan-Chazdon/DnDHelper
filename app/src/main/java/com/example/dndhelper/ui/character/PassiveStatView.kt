package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PassiveStatView(passive: String, value: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            elevation = 5.dp
        ) {
            Text(text= "$passive: $value", modifier = Modifier.padding(start = 5.dp, top = 2.dp, bottom = 2.dp))
        }
    }
}