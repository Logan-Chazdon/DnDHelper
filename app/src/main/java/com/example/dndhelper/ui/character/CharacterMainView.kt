package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun CharacterMainView(characterIndex: Int, viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    Column() {
        Row() {
            TextField(
                value = viewModel.character?.observeAsState()?.value?.name ?: "",
                onValueChange = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.setName(it)
                    }
                }
            )

            Column() {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            viewModel.longRest()
                        }
                    }
                ) {
                    Text("Long rest")
                }
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            viewModel.shortRest()
                        }
                    }
                ) {
                    Text("Short rest")
                }
            }
        }
        FeaturesAndTraitsView(
            features = viewModel.characterFeatures.observeAsState(listOf()).value,
            modifier = Modifier
        )
    }
}