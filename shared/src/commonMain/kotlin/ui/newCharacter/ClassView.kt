package ui.newCharacter


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import gmail.loganchazdon.dndhelper.shared.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun ClassView(
    viewModel: NewCharacterClassViewModel,
    navController: NavController,
    characterId: Int
) {
    val classes by viewModel.classes.collectAsState(emptyList())
    val scope = rememberCoroutineScope() //{ Dispatchers.IO }
    viewModel.id = characterId
    val classIcons = listOf(
        painterResource(Res.drawable.ic_class_icon___artificer),
        painterResource(Res.drawable.ic_class_icon___barbarian),
        painterResource(Res.drawable.ic_class_icon___bard),
        painterResource(Res.drawable.ic_class_icon___cleric),
        painterResource(Res.drawable.ic_class_icon___druid),
        painterResource(Res.drawable.ic_class_icon___fighter),
        painterResource(Res.drawable.ic_class_icon___monk),
        painterResource(Res.drawable.ic_class_icon___paladin),
        painterResource(Res.drawable.ic_class_icon___ranger),
        painterResource(Res.drawable.ic_class_icon___rogue),
        painterResource(Res.drawable.ic_class_icon___sorcerer),
        painterResource(Res.drawable.ic_class_icon___warlock),
        painterResource(Res.drawable.ic_class_icon___wizard)
    )

    val liveCharacter = viewModel.character.collectAsState()

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
                            navController.navigate("newCharacterView/ClassView/ConfirmClassView/${item.id}/$characterId")
                        }
                ) {
                    Row {
                        if(classIcons.getOrNull(i) != null) {
                            Icon(
                                painter = classIcons[i],
                                contentDescription = "${item.name} Icon",
                                modifier = Modifier.padding(
                                    all = 10.dp
                                )
                            )
                        } else {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "${item.name} Icon",
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .size(80.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.h5
                            )

                            Text(
                                text = "d${item.hitDie}",
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                }
            }
        }
    }
}