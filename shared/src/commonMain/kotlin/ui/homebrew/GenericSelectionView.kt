package ui.homebrew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericSelectionView(
    chosen: List<String>,
    onClick : ((Int) -> Unit)? = null,
    onDelete : (Int) -> Unit,
    onExpanded : () -> Unit
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 300.dp),
            state = rememberLazyListState()
        ) {
            itemsIndexed(chosen) { index, it ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.7f).clickable { onClick?.invoke(index) }
                ) {
                    Text(text = it)
                    IconButton(
                        onClick = {
                            onDelete(index)
                        }
                    ) {
                        Icon(Icons.Default.Delete, "Remove")
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                onExpanded()
            }) {
                Text("ADD")
            }
        }
    }
}