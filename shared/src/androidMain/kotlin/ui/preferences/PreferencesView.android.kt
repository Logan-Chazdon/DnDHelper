package ui.preferences

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.SwitchPref
import dataStore
import ui.accounts.GoogleSignInButton
import ui.accounts.SignInManager
import ui.settingsTopBar.SettingsTopBar


@ExperimentalMaterialApi
@SuppressLint("RestrictedApi")
@Composable
actual fun PreferencesView() {
    Scaffold(topBar = { SettingsTopBar() }) { contentPadding ->
        PrefsScreen(dataStore = LocalContext.current.dataStore, Modifier.padding(contentPadding)) {
            prefsGroup("Theme") {
                prefsItem { SwitchPref(key = "dark_mode", title = "Dark mode") }
                prefsItem { SwitchPref(key = "grid_not_row", title = "Full size personality details") }
            }
            prefsGroup("Character Creation") {
                prefsItem { SwitchPref(key = "auto_save", title = "Auto Save", defaultChecked = true) }
            }
            prefsGroup("Account") {
                prefsItem {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        GoogleSignInButton {
                            SignInManager().requestSignIn()
                        }
                    }
                }
            }
        }
    }
}