package com.example.dndhelper.ui.newCharacter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        ) {
            backgrounds.value?.forEachIndexed { i, it ->
                Card(
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(start = 10.dp)
                        .clickable {
                            navController.navigate("newCharacterView/BackgroundView/ConfirmBackGroundView/$i/${viewModel.id}")
                        },
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Text(text = it.name, fontSize = 24.sp, )
                        Text(
                            text = it.desc,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )

                        it.features.forEach { feature ->
                            Text(text = feature.name, fontSize = 18.sp, modifier = Modifier.padding(start = 5.dp))
                            Text(
                                text = feature.description,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 15.dp),
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

                Spacer(modifier =   Modifier.height(10.dp))
            }
        }
    }
}