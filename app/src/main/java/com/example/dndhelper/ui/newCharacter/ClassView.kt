package com.example.dndhelper.ui.newCharacter


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ClassView(
    viewModel: NewCharacterViewModel,
    navController: NavController
) {
    val classes by viewModel.classes.observeAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight(0.95f)
            .fillMaxWidth()
            .background(Color.LightGray)
            .verticalScroll(state = scrollState, enabled = true),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        classes?.withIndex()?.forEach { (i, item) ->
            Card(
                elevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(100.dp)
                    .background(color = Color.Gray, shape = RoundedCornerShape(10.dp))
                    .clickable {
                        navController.navigate("newCharacterView/ClassView/ConfirmClassView/$i")
                    }
            )
            {
              Text(
                  item.name,
                  fontSize = 24.sp,
                )
            }
            if(i != classes!!.size - 1)
            {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}