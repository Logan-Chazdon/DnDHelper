package ui.preferences

import android.annotation.SuppressLint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.SwitchPref

import dataStore
import ui.settingsTopBar.SettingsTopBar


@ExperimentalMaterialApi
@SuppressLint("RestrictedApi")
@Composable
actual fun PreferencesView() {
    Scaffold(topBar = { SettingsTopBar() }) {
       PrefsScreen(dataStore = LocalContext.current.dataStore) {
            prefsGroup("Theme") {
                prefsItem { SwitchPref(key = "dark_mode", title = "Dark mode") }
                prefsItem { SwitchPref(key = "grid_not_row", title = "Full size personality details") }
            }
            prefsGroup("Character Creation") {
                prefsItem { SwitchPref(key = "auto_save", title = "Auto Save", defaultChecked = true) }
            }
        }
    }
}