package gmail.loganchazdon.dndhelper.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun HeathStatsView(
    currentHp: Int,
    maxHp: Int,
    tempHp: Int,
    setHp: (String) -> Unit,
    setTemp: (String) -> Unit,
    heal: () -> Unit,
    addTemp: () -> Unit,
    damage: () -> Unit
) {
    val titles = mapOf(
        "HP" to currentHp,
        "Temp HP" to tempHp,
        "Max HP" to maxHp
    )

    val buttons = listOf(
        "Heal" to heal,
        "Add Temp" to addTemp,
        "Damage" to damage
    )



    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(5.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var i = 0
            for (item in titles) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        elevation = 5.dp,
                        modifier = Modifier.size(85.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.key,
                                style = MaterialTheme.typography.subtitle1
                            )
                            val keyboardController = LocalSoftwareKeyboardController.current
                            val focusController = LocalFocusManager.current

                            var text by remember {
                                mutableStateOf("")
                            }
                            LaunchedEffect(key1 = item.value) {
                                text = item.value.toString()
                            }

                            val onDone = mapOf(
                                "HP" to fun(_: KeyboardActionScope) {
                                    setHp.invoke(text)
                                    keyboardController?.hide()
                                    focusController.clearFocus()
                                },
                                "Temp HP" to fun(_: KeyboardActionScope) {
                                    setTemp.invoke(text)
                                    keyboardController?.hide()
                                    focusController.clearFocus()
                                },
                                "Max HP" to fun(_: KeyboardActionScope) {

                                }
                            )

                            BasicTextField(
                                value = text,
                                modifier = Modifier.padding(5.dp),
                                textStyle = MaterialTheme.typography.h6.copy(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colors.onBackground
                                ),
                                singleLine = true,
                                enabled = i != 2,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = onDone[item.key]
                                ),
                                onValueChange = {
                                    text = it
                                }
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = buttons[i].second
                    ) {
                        Text(buttons[i].first)
                    }
                }
                i += 1
            }
        }
    }
}