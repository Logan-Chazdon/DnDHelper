package ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeathSavesView(
    type: String,
    num: Int?,
    onClick: (Boolean) -> Unit
) {
    Card(
        elevation = 10.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = type)
            Row(
                modifier = Modifier.padding(2.dp)
            ) {
                for (i in 0..2) {
                    Checkbox(
                        checked = (i < num ?: 0),
                        onCheckedChange = onClick
                    )
                    if (i != 2)
                        Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}