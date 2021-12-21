package com.example.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.core.app.ActivityCompat.recreate

import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread





@Composable
fun ConfirmRaceView(viewModel: NewCharacterRaceViewModel, navController: NavController, raceIndex: Int, characterId: Int) {
    val races = viewModel.races.observeAsState()
    val scrollState = rememberScrollState(0)
    viewModel.id = characterId
    val mainLooper = Looper.getMainLooper()
    Column(
        Modifier
            .padding(start = 10.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            races.value?.get(raceIndex)?.let {
                Text(
                    text = it.name,
                    fontSize = 24.sp
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = it.size,
                    fontSize = 16.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        //Change the race
                        GlobalScope.launch{
                            viewModel.setRace(it)
                            //Navigate to the next step
                            Handler(mainLooper).post {
                                navController.navigate("newCharacterView/BackgroundView/${viewModel.id}")
                            }
                        }

                    }) {
                        Text(text = "Set as race", fontSize = 18.sp)
                    }
                }
            }
        }



        if(races.value?.get(raceIndex)?.subraces?.isNotEmpty() == true) {
            var subraceIndex = remember {
                0
            }
            var expanded by remember { mutableStateOf(false) }
            val subrace = races.value!![raceIndex].subraces[subraceIndex]
            Text(text = subrace.name, fontSize = 20.sp, modifier = Modifier.clickable { expanded = true })
            Spacer(Modifier.height(2.dp))
            Text(text = subrace.desc, modifier = Modifier.padding(start = 10.dp))


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                races.value?.get(raceIndex)?.subraces!!.forEachIndexed { index, item ->
                    DropdownMenuItem(onClick = {
                        subraceIndex = index
                        expanded = false
                    }) {
                        Text(text = item.name)
                    }

                }
            }


            subrace.racialTraits.forEach {
                Text(text = it.name, fontSize = 18.sp)
                Spacer(Modifier.height(2.dp))
                Text(text = it.description, modifier = Modifier.padding(start = 10.dp))
            }

        }


        races.value?.get(raceIndex)?.traits?.forEach { trait ->
            Text(text = trait.name, fontSize = 18.sp)
            Spacer(Modifier.height(2.dp))
            Text(text = trait.description, modifier = Modifier.padding(start = 10.dp))
        }




    }
}

