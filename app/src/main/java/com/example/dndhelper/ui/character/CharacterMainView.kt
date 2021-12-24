package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun CharacterMainView(characterIndex: Int, viewModel: CharacterMainViewModel) {
    val character = viewModel.character?.observeAsState()
    Column() {
        TextField(
            value = character?.value?.name ?: "",
            onValueChange = {
                GlobalScope.launch {
                    viewModel.setName(it)
                }
            }
        )

    }
}