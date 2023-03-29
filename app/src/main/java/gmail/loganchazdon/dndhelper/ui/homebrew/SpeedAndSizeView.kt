package gmail.loganchazdon.dndhelper.ui.homebrew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SpeedAndSizeView(
    speed: MutableState<String>,
    sizeClass : MutableState<String>,
    sizeClassOptions : List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = speed.value,
            onValueChange = { speed.value = it },
            singleLine = true,
            label = { Text("Speed") },
            modifier = Modifier.weight(1f, true),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Text("ft")
            }
        )

        var expanded by remember {
            mutableStateOf(false)
        }

        Card(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .weight(1f, true)
                .fillMaxHeight()
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = sizeClass.value,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.h6
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                sizeClassOptions.forEach {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        sizeClass.value = it
                    }) {
                        Text(
                            text = it
                        )
                    }
                }
            }
        }
    }
}