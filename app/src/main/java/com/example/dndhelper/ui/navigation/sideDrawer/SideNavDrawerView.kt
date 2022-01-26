package com.example.dndhelper.ui.navigation.sideDrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dndhelper.ui.navigation.NavItem
import kotlinx.coroutines.CoroutineScope


@ExperimentalMaterialApi
@Composable
fun SideNavDrawer(
    items: List<NavItem>,
    navController: NavHostController,
    onItemClick: (NavItem) -> Unit,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colors.onBackground.copy(0.35f))
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
        )
        items.forEach { item ->
            DrawerNavigationItem(
                onItemClick = onItemClick,
                item = item,
                backStackEntry = backStackEntry,
                scaffoldState,
                scope
            )
        }
    }
}