package com.example.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeathStatsView(currentHp: Int, maxHp: Int, tempHp: Int) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){


        Card(
            elevation = 5.dp,
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "HP",
                    fontSize = 14.sp
                )

                Text(
                    text = currentHp.toString(),
                    modifier = Modifier.padding(5.dp),
                    fontSize = 20.sp
                )
            }
        }

        Text("+")

        Card(
            elevation = 5.dp,
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Temp HP",
                    fontSize = 14.sp
                )


                Text(
                    text = tempHp.toString(),
                    modifier = Modifier.padding(5.dp),
                    fontSize = 20.sp
                )
            }
        }

        Text("/")

        Card(
            elevation = 5.dp,
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Max HP",
                    fontSize = 14.sp
                )


                Text(
                    text = maxHp.toString(),
                    modifier = Modifier.padding(5.dp),
                    fontSize = 20.sp
                )
            }
        }





    }

}