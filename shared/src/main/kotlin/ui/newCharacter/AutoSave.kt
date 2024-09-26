package ui.newCharacter

//import dataStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AutoSave(
    name: String,
    onSave: suspend (MutableState<Int>) -> Unit,
    navController: NavController,
    saveRegardless: Boolean = false
) {
  /*  val autoSaveFlow: Flow<Boolean> = LocalContext.current.dataStore.data.let {
        remember {
            it.map { preferences ->
                preferences[booleanPreferencesKey("auto_save")] ?: false
            }
        }
    }
    if(autoSaveFlow.collectAsState(initial = true).value || saveRegardless) {
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
    }*/
}