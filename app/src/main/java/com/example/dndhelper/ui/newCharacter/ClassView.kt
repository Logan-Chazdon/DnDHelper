package com.example.dndhelper.ui.newCharacter


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dndhelper.R

@Composable
fun ClassView(
    viewModel: NewCharacterClassViewModel,
    navController: NavController,
    characterId: Int
) {
    val classes by viewModel.classes.observeAsState()
    viewModel.id = characterId
    val classIcons = listOf(
        painterResource(R.drawable.ic_class_icon___artificer),
        painterResource(R.drawable.ic_class_icon___barbarian),
        painterResource(R.drawable.ic_class_icon___bard),
        painterResource(R.drawable.ic_class_icon___cleric),
        painterResource(R.drawable.ic_class_icon___druid),
        painterResource(R.drawable.ic_class_icon___fighter),
        painterResource(R.drawable.ic_class_icon___monk),
        painterResource(R.drawable.ic_class_icon___paladin),
        painterResource(R.drawable.ic_class_icon___ranger),
        painterResource(R.drawable.ic_class_icon___rogue),
        painterResource(R.drawable.ic_class_icon___sorcerer),
        painterResource(R.drawable.ic_class_icon___warlock),
        painterResource(R.drawable.ic_class_icon___wizard)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = rememberLazyListState()
    ) {
        classes?.withIndex()?.forEach { (i, item) ->
            item {
                Card(
                    elevation = 5.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(100.dp)
                        .background(
                            color = MaterialTheme.colors.onPrimary
                                .copy(alpha = 0.2f)
                                .compositeOver(MaterialTheme.colors.background),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            navController.navigate("newCharacterView/ClassView/ConfirmClassView/$i/$characterId")
                        }
                ) {
                    Row()
                    {
                        Icon(
                            painter = classIcons[i],
                            contentDescription = "${item.name} Icon",
                            modifier = Modifier.padding(
                                all = 10.dp
                            )
                        )
                        Text(
                            item.name,
                            fontSize = 24.sp,
                        )
                    }
                }
            }
        }
    }
}