package ui.platformSpecific

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenHeight(): Dp {
    return  LocalConfiguration.current.screenHeightDp.dp
}

@Composable
actual fun getScreenWidth(): Dp {
   return LocalConfiguration.current.screenWidthDp.dp
}