package gmail.loganchazdon.dndhelper.ui.navigation.sideDrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BadgedBox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import gmail.loganchazdon.dndhelper.ui.navigation.NavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun DrawerNavigationItem(
    onItemClick: (NavItem) -> Unit,
    item: NavItem,
    backStackEntry: State<NavBackStackEntry?>,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) {
    val selected =
        item.baseRoute.split("/")[0] == (backStackEntry.value?.destination?.route?.split("/")
            ?.get(0) ?: "")
    Row(
        modifier = Modifier
            .clickable {
                onItemClick(item)
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }
            .fillMaxWidth()
            .height(50.dp)
            .background(if (selected) {
                MaterialTheme.colors.onSurface.copy(0.15f)
            } else MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                modifier = Modifier.size(30.dp),
                tint = (if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground.copy(0.5f))
            )
        } else {
            Icon(
                painter = item.painter!!,
                contentDescription = item.name,
                modifier = Modifier.size(30.dp),
                tint = (if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground.copy(0.5f))
            )
        }

        if (item.badgeCount > 0) {
            BadgedBox(
                badge = { Text(text = item.badgeCount.toString()) }
            ) {
                icon
            }
        } else {
            icon
        }
        Spacer(
            modifier = Modifier
                .size(35.dp)
        )

        Text(
            text = item.name,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
            fontStyle = if (selected) FontStyle.Italic else FontStyle.Normal
        )
    }
}

