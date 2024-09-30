package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultipleFABView(
    content: @Composable () -> Unit,
    items : List<@Composable (Modifier) -> Unit>
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(expanded) {
            items.forEach {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    it(Modifier.padding(5.dp))
                }
            }
        }

        FloatingActionButton(onClick = {
            expanded = !expanded
        }) {
            content()
        }
    }
}