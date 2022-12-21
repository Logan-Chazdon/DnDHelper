package gmail.loganchazdon.dndhelper.ui.newCharacter


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ClassView(
    viewModel: NewCharacterClassViewModel,
    navController: NavController,
    characterId: Int
) {
    val classes by viewModel.classes.observeAsState()
    val scope = rememberCoroutineScope { Dispatchers.IO }
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

    val liveCharacter = viewModel.character.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = rememberLazyListState()
    ) {
        liveCharacter.value?.let { character ->
            if (character.classes.isNotEmpty()) {
                character.classes.forEach { (_, clazz) ->
                    item {
                        val classIndex = classes?.indexOfFirst { it.name == clazz.name }!!
                        Card(
                            elevation = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .height(50.dp)
                                .clickable {
                                    navController.navigate("newCharacterView/ClassView/ConfirmClassView/${clazz.id}/$characterId")
                                }
                        ) {
                            var deleteClassIsExpanded by remember {
                                mutableStateOf(false)
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${clazz.name}, ${clazz.level}",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(4.dp)
                                )

                                Box(
                                    modifier = Modifier.clickable {
                                        deleteClassIsExpanded = !deleteClassIsExpanded
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove class",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            if(deleteClassIsExpanded) {
                                Dialog(onDismissRequest = { deleteClassIsExpanded = false }) {
                                    Box(
                                        Modifier
                                        .background(
                                            color = MaterialTheme.colors.surface,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = "Delete: ${clazz.name}",
                                                fontSize = 20.sp
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Button(
                                                    onClick = { deleteClassIsExpanded = false }
                                                ) {
                                                    Text(text = "Cancel")
                                                }

                                                Button(
                                                    onClick = {
                                                        scope.launch {
                                                            viewModel.removeClass(clazz.id)
                                                        }
                                                        deleteClassIsExpanded = false
                                                    }
                                                ) {
                                                    Text(text = "Delete")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Divider()
                }
            }
        }

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