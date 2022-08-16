package gmail.loganchazdon.dndhelper.ui.homebrew

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.R
import gmail.loganchazdon.dndhelper.ui.MultipleFABView

@Composable
fun HomebrewView(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            MultipleFABView(
                content = {
                    Icon(Icons.Default.Add, "New homebrew")
                },
                items = listOf(
                    {
                        FloatingActionButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painterResource(R.drawable.ic_class_icon___ranger),
                                "New class",
                                it
                            ) //Todo make a class icon
                        }
                    },
                    {
                        FloatingActionButton(onClick = { navController.navigate("homebrewView/homebrewRaceView/-1") }) {
                            Icon(
                                painterResource(R.drawable.ic_race_icon),
                                "New race",
                                it
                            )
                        }
                    }
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            //TODO all homebrew.
        }
    }
}