package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun CharacterMainView( viewModel: CharacterMainViewModel) {
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.fillMaxSize(0.97f)
        ) {
            Column() {
                Row() {
                    TextField(
                        value = viewModel.character?.observeAsState()?.value?.name ?: "",
                        onValueChange = {
                            scope.launch(Dispatchers.IO) {
                                viewModel.setName(it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                    FeaturesAndTraitsView(
                        features = viewModel.characterFeatures.observeAsState(listOf()).value,
                        modifier = Modifier.fillMaxHeight(0.4f)
                    )
                }
            }
        }
    }
}