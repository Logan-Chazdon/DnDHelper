package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*


@Composable
fun CharacterMainView(characterIndex: Int, viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    Column() {
        TextField(
            value = viewModel.character?.observeAsState()?.value?.name ?: "",
            onValueChange = {
                scope.launch(Dispatchers.IO) {
                    viewModel.setName(it)
                }
            }
        )

    }
}