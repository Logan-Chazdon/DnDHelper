package com.example.dndhelper.ui.navigation.sideDrawer

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.example.dndhelper.ui.navigation.NavItem
import com.example.dndhelper.ui.theme.Purple700
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
        item.baseRoute.split("/")?.get(0) == backStackEntry.value?.destination?.route?.split("/")
            ?.get(0) ?: ""
    Row(
        modifier = Modifier
            .clickable {
                onItemClick(item)
                scope.launch {
                    //scaffoldState.drawerState.close(
                    scaffoldState.drawerState.animateTo(
                        targetValue = DrawerValue.Closed,
                        anim = tween(
                            durationMillis = 300,
                            delayMillis = 0,
                            easing = FastOutLinearInEasing
                        )
                    )
                }
            }
            .fillMaxWidth()
            .height(50.dp)
            .background(if (selected) Color.LightGray else Color.White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                modifier = Modifier.size(30.dp),
                tint = (if (selected) Purple700 else Color.Gray)
            )
        } else {
            Icon(
                painter = item.painter!!,
                contentDescription = item.name,
                modifier = Modifier.size(30.dp),
                tint = (if (selected) Purple700 else Color.Gray)
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
            color = if (selected) Purple700 else Color.Black,
            fontStyle = if (selected) FontStyle.Italic else FontStyle.Normal
        )
    }
}

