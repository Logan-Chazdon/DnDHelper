package gmail.loganchazdon.dndhelper.ui.preferences

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.SwitchPref
import gmail.loganchazdon.dndhelper.dataStore


@ExperimentalMaterialApi
@SuppressLint("RestrictedApi")
@Composable
fun PreferencesView() {
    Scaffold(topBar = { SettingsTopBar() }) {
        PrefsScreen(dataStore = LocalContext.current.dataStore, Modifier.padding(it)) {
            prefsGroup("Theme") {
                prefsItem { SwitchPref(key = "dark_mode", title = "Dark mode") }
                prefsItem { SwitchPref(key = "grid_not_row", title = "Full size personality details", defaultChecked = false) }
            }
            prefsGroup("Character Creation") {
                prefsItem { SwitchPref(key = "auto_save", title = "Auto Save", defaultChecked = true) }
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.subtitle1
            )
        }
    )
}