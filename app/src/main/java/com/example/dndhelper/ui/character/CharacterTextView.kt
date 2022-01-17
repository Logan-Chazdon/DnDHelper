package com.example.dndhelper.ui.character

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun CharacterTextView(
    name: String,
    value: String,
    onChange: (String) -> Unit
) {
    Text(text = name)
    TextField(
        value = value,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
}