package gmail.loganchazdon.dndhelper.ui.homebrew

import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomebrewRaceView(
    navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Add, "New Feature")
            }
        }
    ) {
        Column(Modifier.padding(it)) {

        }
    }
}