package ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun CharacterTextView(
    name: String,
    value: String,
    onChange: (String) -> Unit,
    modifier : Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(text = name)
        TextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxSize(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}