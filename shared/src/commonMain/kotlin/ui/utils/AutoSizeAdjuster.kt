package ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ui.platformSpecific.getScreenWidth

class AutoSizeAdjuster(
    val size : TextUnit
) {
    @Composable
    fun value() : TextUnit {
        val default = getScreenWidth() / 1100
        return size.value.times(default.value).sp
    }
}