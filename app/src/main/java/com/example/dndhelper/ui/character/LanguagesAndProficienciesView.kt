package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dndhelper.repository.dataClasses.Language
import com.example.dndhelper.repository.dataClasses.Proficiency

@Composable
fun LanguagesAndProficienciesView(
    languages: List<Language>,
    proficiencies: List<Proficiency>,
    modifier : Modifier
) {
    Card(
        modifier = modifier,
        elevation = 5.dp
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            state = listState
        ) {
            items(items = languages) { item: Language ->
                item.name?.let { Text(text = it) }
                Divider(thickness = (0.5).dp, startIndent = 10.dp)
            }

            items(items = proficiencies) { item: Proficiency ->
                item.name?.let { Text(text = it) }
                Divider(thickness = (0.5).dp, startIndent = 10.dp)
            }
        }
    }
}