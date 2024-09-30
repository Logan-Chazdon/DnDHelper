package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gmail.loganchazdon.dndhelper.shared.generated.resources.*
import kotlinx.coroutines.flow.count
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import ui.character.AllCharactersViewModel
import ui.navigation.NavItem
import ui.navigation.Navigation
import ui.navigation.bottomNavBar.BottomNavigationBar
import ui.navigation.sideDrawer.SideNavDrawer


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun RootView(allCharactersViewModel: AllCharactersViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(allCharactersViewModel.allCharacters.collectAsState(emptyList()).value.size) {
        if(allCharactersViewModel.allCharacters.count() == 0) {
            scaffoldState.drawerState.open()
        }
    }

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
                        ),
                        NavItem(
                            name = "Homebrew",
                            route = "homebrewView",
                            baseRoute = "homebrewView",
                            icon = Icons.Default.Home
                        ),
                        NavItem(
                            name = "Settings",
                            route = "preferences",
                            baseRoute = "preferences",
                            icon = Icons.Default.Settings
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
                                painter = painterResource(Res.drawable.ic_race_icon)
                            ),
                            NavItem(
                                name = "Background",
                                route = "$route/BackgroundView/$id",
                                baseRoute = "$route/BackgroundView",
                                painter = painterResource(Res.drawable.ic_background_icon)
                            ),
                            NavItem(
                                name = "Stats",
                                route = "$route/StatsView/$id",
                                baseRoute = "$route/StatsView",
                                painter = painterResource(Res.drawable.ic_stats_icon)
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
                        items = listOf(
                            NavItem(
                                name = "Character",
                                route = "$route/MainView/$id",
                                baseRoute = "$route/MainView",
                                icon = Icons.Default.Home
                            ),
                            NavItem(
                                name = "Stats",
                                route = "$route/StatsView/$id",
                                baseRoute = "$route/StatsView",
                                painter = painterResource(Res.drawable.ic_stats_icon)
                            ),
                            NavItem(
                                name = "Combat",
                                route = "$route/CombatView/$id",
                                baseRoute = "$route/CombatView",
                                painter = painterResource(Res.drawable.ic_combat_icon)
                            ),
                            NavItem(
                                name = "Items",
                                route = "$route/ItemsView/$id",
                                baseRoute = "$route/ItemsView",
                                painter = painterResource(Res.drawable.ic_items_icon)
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
    ) { innerPadding ->
        //Apply the padding globally
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            Navigation(navController = navController)
        }
    }
}