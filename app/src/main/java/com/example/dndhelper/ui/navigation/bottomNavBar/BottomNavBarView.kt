package com.example.dndhelper.ui.navigation.bottomNavBar

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dndhelper.ui.navigation.NavItem

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<NavItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onItemClick: (NavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.DarkGray,
        elevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = backStackEntry.value?.destination?.route?.contains(item.baseRoute) ?: false
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item)},
                selectedContentColor = Color.Green,
                unselectedContentColor = Color.Gray,
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if(item.badgeCount > 0){
                            BadgeBox(
                                badgeContent = {
                                    Text(text = item.badgeCount.toString())
                                }
                            ) {
                                if(item.icon != null) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.name
                                    )
                                } else {
                                    Icon(
                                        painter = item.painter!!,
                                        contentDescription = item.name
                                    )
                                }
                            }
                        } else {
                            if(item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name
                                )
                            } else {
                                Icon(
                                    painter = item.painter!!,
                                    contentDescription = item.name
                                )
                            }
                        }
                        if(selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize =  10.sp
                            )
                        }
                    }
                })
        }
    }
}

