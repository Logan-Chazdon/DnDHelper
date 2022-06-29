package com.example.dndhelper.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dndhelper.ui.utils.Dimensions
import com.example.dndhelper.ui.utils.MediaQuery
import com.example.dndhelper.ui.utils.greaterThan

@Composable
fun StatBoxView(stat: String, value: Int, mod: Int, onClick: () -> Unit) {
    var sizeMod = 1
    MediaQuery(Dimensions.Width greaterThan 800.dp) {
        sizeMod = 2
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Card(
            modifier = Modifier
                .size((100 * sizeMod).dp)
                .background(
                    color = MaterialTheme.colors.surface
                )
                .clickable { onClick() },
            elevation = 10.dp,
            shape = RoundedCornerShape((10* sizeMod).dp),
        ) {
            Column(

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))
                Text(
                    text = stat
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.12f * sizeMod))
                Box(
                  modifier = Modifier
                      .size((40 * sizeMod).dp)
                      .background(
                          shape = CutCornerShape((10 * sizeMod).dp),
                          color = MaterialTheme.colors.onSurface
                              .copy(alpha = 0.3f)
                      ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString()
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .offset(y = (-13).dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colors.onBackground
                        .copy(alpha = 0.2f)
                        .compositeOver(MaterialTheme.colors.background)
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp)
            )
            {
                var modString = mod.toString()
                if(mod > 0) {
                    modString = "+$mod"
                }
                Text(text = modString, textAlign = TextAlign.Center,
                    modifier = Modifier.width(25.dp))
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    StatBoxView("Strength", 20, 5) {}
}