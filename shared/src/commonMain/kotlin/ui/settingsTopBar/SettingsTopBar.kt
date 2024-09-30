package ui.settingsTopBar

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
internal fun SettingsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.subtitle1
            )
        }
    )
}