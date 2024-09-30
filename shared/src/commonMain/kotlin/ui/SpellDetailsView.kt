package ui


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.unit.dp
import model.Item
import model.Spell
import ui.utils.allNames


@Composable
fun SpellDetailsView(
    spell: Spell
) {
    Column{
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = spell.name,
                    style = MaterialTheme.typography.h5
                        .copy(fontStyle = Italic),
                    modifier = Modifier.padding(4.dp)
                )

                if (spell.isRitual) {
                    Box(contentAlignment = Alignment.Center) {
                        val surface = MaterialTheme.colors.surface
                        val onSurface = MaterialTheme.colors.onSurface
                        Canvas(modifier = Modifier.size(15.dp)) {
                            drawCircle(
                                color = surface,
                                center = this.center,
                                style = Fill
                            )

                            drawCircle(
                                color = onSurface,
                                center = this.center,
                                style = Stroke(2f)
                            )
                        }
                        Text(
                            text = "R",
                            style = MaterialTheme.typography
                                .body2.copy(fontStyle = Italic)
                        )
                    }
                }
            }

            Row {
                spell.components.forEach {
                    Text(
                        text = it[0].toString(),
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(spell.school)
                Text(spell.levelName)
            }
            Column {
                Text(spell.castingTime)
                Text(spell.duration)
            }
            Column {
                Text(spell.range)
                Text(spell.area)
            }
        }

        Column(
            modifier = Modifier
                .heightIn(min = 50.dp, max = 400.dp)
                .verticalScroll(rememberScrollState())
        ){
            Text(
                text = spell.desc,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(4.dp),
            )
        }

        if(spell.itemComponents.isNotEmpty()) {
            Text(
                text = "${spell.itemComponents.allNames}.",
                style = MaterialTheme.typography.overline,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

//@Preview
@Composable
fun PreviewSpellDetailsView() {
    Card {
        SpellDetailsView(
            spell = Spell(
                isRitual = true,
                name = "Eldritch Blast",
                level = 0,
                castingTime = "1 Bonus Action",
                classes = listOf("Warlock"),
                duration = "Instantaneous",
                damage = "1d10+1d8",
                components = listOf("Verbal", "Somatic"),
                itemComponents = listOf(Item("A newts foot")),
                area = "20 foot radius 40 foot tall cylinder",
                range = "120 feet",
                school = "Evocation",
                desc = "A beam of crackling energy streaks toward a creature within range. Make a ranged spell attack against the target. " +
                        "On a hit, the target takes 1d10 force damage.\n The spell creates more than one beam when you reach higher levels: " +
                        "two beams at 5th level, three beams at 11th level, and four beams at 17th level. You can direct the beams at the same" +
                        " target or at different ones. Make a separate attack roll for each beam.\n",
            )
        )
    }
}