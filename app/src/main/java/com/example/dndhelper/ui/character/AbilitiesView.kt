package com.example.dndhelper.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@ExperimentalFoundationApi
@Composable
fun AbilitiesView(viewModel: AbilitiesViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        viewModel.character?.observeAsState()?.value?.let {
            if (it.isCaster) {
                SpellCastingView(
                    character = it,
                    modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.6f)
                )
            }
        }
    }
}
