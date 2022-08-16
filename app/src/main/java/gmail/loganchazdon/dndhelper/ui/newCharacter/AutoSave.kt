package gmail.loganchazdon.dndhelper.ui.newCharacter

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.dataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AutoSave(
    name: String,
    onSave: suspend (MutableState<Int>) -> Unit,
    navController: NavController
) {
    val autoSaveFlow: Flow<Boolean> = LocalContext.current.dataStore.data.let {
        remember {
            it.map { preferences ->
                preferences[booleanPreferencesKey("dark_mode")] ?: false
            }
        }
    }
    if(autoSaveFlow.collectAsState(initial = true).value) {
        DisposableEffect(true) {
            val listener =
                NavController.OnDestinationChangedListener { _, destination, arguments ->
                    GlobalScope.launch {
                        if (destination.route?.contains(name) != true) {
                            val id = mutableStateOf(-1)
                            onSave(id)
                            arguments!!.putString("characterId", id.value.toString())
                        }
                    }
                }
            navController.addOnDestinationChangedListener(listener)
            onDispose {
                navController.removeOnDestinationChangedListener(listener)
            }
        }
    }
}