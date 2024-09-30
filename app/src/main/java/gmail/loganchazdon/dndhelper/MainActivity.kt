package gmail.loganchazdon.dndhelper

//import androidx.datastore.kt.core.DataStore
//import androidx.datastore.kt.preferences.core.Preferences
//import androidx.datastore.kt.preferences.preferencesDataStore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import ui.RootView
import ui.theme.DnDHelperTheme



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