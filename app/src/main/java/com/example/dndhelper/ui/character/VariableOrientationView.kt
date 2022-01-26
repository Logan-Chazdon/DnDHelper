package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun VariableOrientationView(
    isVertical: Boolean,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    content: @Composable () -> Unit,
) {
    if(isVertical) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = arrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content.invoke()
        }
    } else {
        Row (
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = arrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content.invoke()
        }
    }
}