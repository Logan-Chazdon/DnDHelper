package com.example.dndhelper.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dndhelper.ui.character.AllCharactersView
import com.example.dndhelper.ui.navigation.NavItem
import com.example.dndhelper.ui.navigation.Navigation
import com.example.dndhelper.ui.navigation.bottomNavBar.BottomNavigationBar
import com.example.dndhelper.ui.navigation.sideDrawer.SideNavDrawer

@ExperimentalMaterialApi
@Composable
fun RootView() {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            SideNavDrawer(
                items = listOf(
                        NavItem(
                            name = "My Characters",
                            route = "allCharactersView",
                            baseRoute = "allCharactersView",
                            icon = Icons.Default.Person
                        ),
                        NavItem(
                            name = "New Character",
                            route = "newCharacterView/ClassView/-1",
                            baseRoute = "newCharacterView/ClassView",
                            icon = Icons.Default.Add
                        )
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                },
                scaffoldState = scaffoldState,
                scope = scope
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            when (val route = navBackStackEntry?.destination?.route?.split("/")?.get(0)) {
                "newCharacterView" -> {
                    val id : Int = navBackStackEntry?.arguments?.getString("characterId")?.toInt() ?: -1
                    BottomNavigationBar(
                        //TODO add custom icons
                        items = listOf(
                            NavItem(
                                name = "Class",
                                route = "$route/ClassView/$id",
                                baseRoute = "$route/ClassView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Race",
                                route = "$route/RaceView/$id",
                                baseRoute = "$route/RaceView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Background",
                                route = "$route/BackgroundView/$id",
                                baseRoute = "$route/BackgroundView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Stats",
                                route = "$route/StatsView/$id",
                                baseRoute = "$route/StatsView",
                                icon = Icons.Default.Home
                            )
                        ),
                        navController = navController,
                        onItemClick = {
                            navController.navigate(it.route)
                        }
                    )
                }
                "characterView" -> {
                    val id : Int = navBackStackEntry?.arguments?.getString("characterId")?.toInt() ?: -1
                    BottomNavigationBar(
                        //TODO add custom icons
                        items = listOf(
                            NavItem(
                                name = "Character",
                                route = "$route/MainView/$id",
                                baseRoute = "$route/MainView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Abilities",
                                route = "$route/AbilitiesView/$id",
                                baseRoute = "$route/AbilitiesView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Stats",
                                route = "$route/StatsView/$id",
                                baseRoute = "$route/StatsView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Combat",
                                route = "$route/CombatView/$id",
                                baseRoute = "$route/CombatView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Items",
                                route = "$route/ItemsView/$id",
                                baseRoute = "$route/ItemsView",
                                icon = Icons.Default.Home
                            )
                        ),
                        navController = navController,
                        onItemClick = {
                            navController.navigate(it.route)
                        }
                    )
                }
            }
        }
    ) {
        Navigation(navController = navController)
    }

}