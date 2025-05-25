package ui.newCharacter

import androidx.compose.runtime.*
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ui.preferences.DataStore

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AutoSave(
    name: String,
    onSave: suspend (MutableState<Int>) -> Unit,
    navController: NavController,
    saveRegardless: Boolean = false
) {
    val autoSaveFlow: Flow<Boolean> = DataStore.autoSave()
    if (autoSaveFlow.collectAsState(initial = true).value || saveRegardless) {
        DisposableEffect(true) {
            val id = mutableStateOf(-1)
            val listener =
                NavController.OnDestinationChangedListener { _, destination, _ ->
                    GlobalScope.launch {
                        if (destination.route?.contains(name) != true) {
                            onSave(id)
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