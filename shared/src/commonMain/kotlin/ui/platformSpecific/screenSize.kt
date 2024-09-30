package ui.platformSpecific

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
@Suppress("no_actual_for_expect")
expect fun getScreenWidth(): Dp


@Composable
@Suppress("no_actual_for_expect")
expect fun getScreenHeight(): Dp

@Composable
fun isVertical() = getScreenWidth() < getScreenHeight()