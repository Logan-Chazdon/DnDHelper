package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.trimmedLength
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.ui.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatsView(
    viewModel: NewCharacterStatsViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val mainLooper = Looper.getMainLooper()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("DONE") },
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.longRest()
                        Handler(mainLooper).post {
                            navController.navigate("characterView/MainView/${viewModel.id}")
                        }
                    }
                })
        }
    ) {
    Column {
        var statGenDropdownExpanded by remember { mutableStateOf(false) }
        val selectedIndexStatGen = viewModel.currentStateGenTypeIndex.observeAsState()
        val statsOptions = viewModel.currentStatsOptions.observeAsState()
        val stats = viewModel.currentStats.observeAsState()
        val statGenOptions = listOf("Point Buy", "Standard Array", "Rolled", "Manual")
        Box {
            Text(
                text = statGenOptions[selectedIndexStatGen.value ?: 0],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { statGenDropdownExpanded = true })
                    .background(MaterialTheme.colors.surface),
                fontSize = 20.sp
            )

            DropdownMenu(
                expanded = statGenDropdownExpanded,
                onDismissRequest = { statGenDropdownExpanded = false }) {
                statGenOptions.forEachIndexed { index, item ->
                    DropdownMenuItem(onClick = {
                        viewModel.setCurrentStatGenTypeIndex(index)
                        statGenDropdownExpanded = false
                    }) {
                        Text(text = item, fontSize = 20.sp)
                    }
                }
            }
        }

        val pointsRemaining = viewModel.pointsRemaining.observeAsState()
        if (selectedIndexStatGen.value == 0) {
            Text(
                text = "Points Remaining: ${pointsRemaining.value}"
            )
        }
        

        val statNames = listOf(
            "Str", "Dex", "Con", "Int", "Wis", "Cha"
        )

        var columns = 6
        var rows = 1
        MediaQuery(Dimensions.Height lessThan 500.dp) {
            columns = 3
            rows = 2
        }
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0 until rows) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .mediaQuery(
                            Dimensions.Height lessThan 500.dp,
                            Modifier.width(screenWidth.times(0.48f))
                        )
                        .mediaQuery(
                            Dimensions.Height greaterThan 500.dp,
                            Modifier.fillMaxWidth(0.9f)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    val selectedIndexes = viewModel.selectedStatIndexes.observeAsState()
                    for (column in 0 until columns) {

                        var statChoiceExpanded by remember { mutableStateOf(false) }
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            elevation = 5.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .padding(top = 23.dp, bottom = 23.dp, start = 15.dp)
                                        .fillMaxWidth(0.15f)
                                ) {
                                    Text(
                                        text = "${statNames[(column * rows) + row]}: ",
                                        fontSize = 20.sp
                                    )
                                }


                                if(viewModel.currentStateGenTypeIndex.observeAsState().value == 3) {
                                    val textFieldValue = remember {
                                        mutableStateOf("")
                                    }
                                    val isError = remember {
                                        mutableStateOf(false)
                                    }

                                    val focusManager = LocalFocusManager.current
                                    val convertToNumber = fun(input: String): Int {
                                        return try {
                                            input.toInt()
                                        } catch(_: NumberFormatException) {
                                            0
                                        }
                                    }

                                    OutlinedTextField(
                                        value = textFieldValue.value,
                                        onValueChange = {
                                            textFieldValue.value = it
                                            isError.value = convertToNumber(it) !in 3..18
                                            if(!isError.value) {
                                                val tempList = viewModel.currentStats.value!!.toMutableList()
                                                tempList[(column * rows) + row] = convertToNumber(it)
                                                viewModel.currentStats.postValue(tempList)
                                            }
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = {
                                                focusManager.moveFocus(focusDirection = FocusDirection.Next)
                                            }
                                        ),
                                        isError = isError.value,
                                        modifier = Modifier
                                            .width(150.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = { statChoiceExpanded = true })
                                    ) {
                                        Text(
                                            text = try {
                                                stats.value?.get(selectedIndexes.value!![(column * rows) + row])
                                                    .toString()
                                            } catch (e: IndexOutOfBoundsException) {
                                                "0"
                                            },
                                            modifier = Modifier
                                                .padding(23.dp),
                                            fontSize = 20.sp
                                        )
                                    }



                                    DropdownMenu(
                                        expanded = statChoiceExpanded,
                                        onDismissRequest = { statChoiceExpanded = false }) {
                                        statsOptions.value?.forEachIndexed { index, item ->
                                            DropdownMenuItem(onClick = {
                                                viewModel.selectedStatByIndex(
                                                    (column * rows) + row,
                                                    index
                                                )
                                                statChoiceExpanded = false
                                            }) {
                                                Text(text = item.toString(), fontSize = 20.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}