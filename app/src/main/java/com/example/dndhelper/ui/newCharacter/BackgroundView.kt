package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun BackgroundView(
    characterId: Int,
    viewModel: NewCharacterBackgroundViewModel,
    navController: NavHostController
) {
    val backgrounds = viewModel.backgrounds.observeAsState()
    val scrollState = rememberScrollState()
    viewModel.id = characterId

    Row(
       modifier = Modifier
           .fillMaxSize(),
       horizontalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            backgrounds.value?.forEachIndexed { i, it ->
                Card(
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clickable {
                            navController.navigate("newCharacterView/BackgroundView/ConfirmBackGroundView/$i/${viewModel.id}")
                        },
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Text(text = it.name, style = MaterialTheme.typography.h5)
                        Text(
                            text = it.desc,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )

                        it.features.forEach { feature ->
                            Text(text = feature.name, style = MaterialTheme.typography.h6)
                            Text(
                                text = feature.description,
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.padding(start = 5.dp),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2
                            )
                        }

                        if(it.languages.isNotEmpty()) {
                            Row(modifier = Modifier.padding(start = 5.dp))
                            {
                                Text(
                                    "Languages: "
                                )
                                it.languages.forEach { lang ->
                                    Text(
                                        lang.name.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}