package gmail.loganchazdon.dndhelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import gmail.loganchazdon.dndhelper.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val DarkColorPalette = darkColors(
    primary = Purple700,
    primaryVariant = Purple500,
    secondary = Teal700,
    background = Color.Black,
    onBackground = Color.White,
    onSurface = Color.LightGray,
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.White
)


@Composable
fun DnDHelperTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val overrideDarkModeFlow: Flow<Boolean> = LocalContext.current.dataStore.data.let {
        remember {
            it.map { preferences ->
                preferences[booleanPreferencesKey("dark_mode")] ?: false
            }
        }
    }

    val colors = if (darkTheme || overrideDarkModeFlow.collectAsState(initial = false).value) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}