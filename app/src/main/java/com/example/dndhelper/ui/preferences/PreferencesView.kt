package com.example.dndhelper.ui.preferences

import android.annotation.SuppressLint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dndhelper.dataStore
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.SwitchPref


@ExperimentalMaterialApi
@SuppressLint("RestrictedApi")
@Composable
fun PreferencesView() {
    PrefsScreen(dataStore = LocalContext.current.dataStore) {
        prefsItem { SwitchPref(key = "dark_mode", title = "Dark mode") }
    }
}