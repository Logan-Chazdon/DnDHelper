package gmail.loganchazdon.dndhelper.ui.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProficienciesBoxView(
    baseStat: String,
    baseStatNum: Int,
    profBonus: Int,
    stats: Map<String, Int>,
    modifier : Modifier
) {
    Card(
        modifier = modifier,
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = baseStat, fontSize = 18.sp)
            stats.forEach {
                Spacer(Modifier.height(20.dp))

                //TODO  make three skill views for different amounts of space
                //TODO make a separate color for expertise
                LargeSkillView(
                    checked = it.value != 0,
                    value = (baseStatNum + profBonus * it.value),
                    name = it.key
                )

            }
        }
    }
}


@Composable
private fun LargeSkillView(
    checked: Boolean,
    value: Int,
    name: String
) {
    Box {
        val onBackground = MaterialTheme.colors.onBackground
        val selected = MaterialTheme.colors.onSurface.copy(0.5f).compositeOver(MaterialTheme.colors.surface)
        val surface = MaterialTheme.colors.surface

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.absoluteOffset(x = 20.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(30.dp)
            ) {
                drawRoundRect(
                    color = onBackground,
                    size = Size(width = size.width, height = size.height),
                    style = Stroke(2f),
                    cornerRadius =  CornerRadius(x = 10f, y = 10f)
                )
            }
            Text(text = name, modifier = Modifier.absoluteOffset(x = 20.dp))
        }

        Box(
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(30.dp)
            ) {
                drawCircle(
                    color = if(checked) { selected } else { surface },
                    center = Offset(x = size.width / 2, y = size.height / 2),
                    radius = size.minDimension / 1.5f,
                    style = Fill
                )
                drawCircle(
                    color = onBackground,
                    center = Offset(x = size.width / 2, y = size.height / 2),
                    radius = size.minDimension / 1.5f,
                    style = Stroke(2f)
                )
            }
            Text(value.toString())
        }
    }
}

