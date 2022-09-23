package gmail.loganchazdon.dndhelper.ui.homebrew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gmail.loganchazdon.dndhelper.model.Feature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FeaturesView(features: List<Feature>, onDelete: (Int) -> Unit, onClick: (Int) -> Unit) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .padding(5.dp),
        ) {
            features.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.name.ifBlank { "Unnamed feature" },
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .clickable { onClick(feature.featureId) }
                            .padding(start = 5.dp)
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                onDelete(feature.featureId)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Remove Feature"
                        )
                    }
                }
            }
        }
    }
}