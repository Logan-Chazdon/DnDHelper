package com.example.dndhelper.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Teal700 = Color(0xFF368D85)

val Colors.noActionNeeded : Color
    @Composable
    get() = MaterialTheme.colors.onBackground.copy(alpha = 0.3f)
        .compositeOver(MaterialTheme.colors.background)
