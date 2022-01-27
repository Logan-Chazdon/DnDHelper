package com.example.dndhelper.ui.preferences

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dndhelper.dataStore
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.SwitchPref


@ExperimentalMaterialApi
@SuppressLint("RestrictedApi")
@Composable
fun PreferencesView() {
    Scaffold(topBar = { SettingsTopBar() }) {
        PrefsScreen(dataStore = LocalContext.current.dataStore) {
            prefsGroup("Theme") {
                prefsItem { SwitchPref(key = "dark_mode", title = "Dark mode") }
                prefsItem { SwitchPref(key = "grid_not_row", title = "Full size personality details") }
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