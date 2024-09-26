package gmail.loganchazdon.dndhelper

//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import ui.RootView
import ui.theme.DnDHelperTheme

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DnDHelperTheme {
                RootView()
            }
        }
    }
}



@ExperimentalComposeUiApi
@Composable
fun DefaultPreview() {
    DnDHelperTheme {
        RootView()
    }
}