package ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun VariableOrientationView(
    isVertical: Boolean,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    verticalAlignment : Alignment.Vertical? = null,
    horizontalAlignment : Alignment.Horizontal? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if(isVertical) {
        Column (
            modifier = modifier.fillMaxSize(),
            verticalArrangement =  arrangement,
            horizontalAlignment = horizontalAlignment ?: Alignment.CenterHorizontally
        ) {
            content.invoke()
        }
    } else {
        Row (
            modifier = modifier.fillMaxSize(),
            horizontalArrangement =arrangement,
            verticalAlignment = verticalAlignment ?: Alignment.CenterVertically
        ) {
            content.invoke()
        }
    }
}