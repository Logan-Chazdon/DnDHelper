package ui.homebrew

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun <T> GenericSelectionPopupView(
    items: List<T>,
    onItemClick: (T) -> Unit,
    detailsView: @Composable ((T) -> Unit)?,
    isExpanded: MutableState<Boolean>,
    getName: (T) -> String,
    isSelected: (T) -> Boolean
) {
    if (isExpanded.value) {
        Dialog(
            onDismissRequest = { isExpanded.value = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier.fillMaxSize(0.9f)
            ) {
                Column {
                    var search by remember {
                        mutableStateOf("")
                    }
                    TextField(
                        value = search,
                        onValueChange = {
                            search = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Search")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                "Search"
                            )
                        }
                    )

                    LazyColumn(
                        state = rememberLazyListState(),
                    ) {
                        items(items.let {
                            if (search.isNotBlank()) {
                                it.filter { item ->
                                    item.toString().contains(search, true)
                                }
                            } else {
                                it
                            }
                        }) { item ->
                            var expanded by remember {
                                mutableStateOf(false)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RectangleShape,
                                backgroundColor = if (isSelected(item)) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.surface
                                }
                            ) {
                                Text(
                                    text = getName(item),
                                    modifier = Modifier.combinedClickable(
                                        onClick = {
                                            onItemClick(item)
                                        },
                                        onLongClick = {
                                            expanded = !expanded
                                        }
                                    ).padding(start = 5.dp)
                                )
                            }

                            if (detailsView != null) {
                                if (expanded) {
                                    Dialog(
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(0.9f)
                                        ) {
                                            detailsView(item)
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
