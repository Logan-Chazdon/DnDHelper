package gmail.loganchazdon.dndhelper.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gmail.loganchazdon.dndhelper.R
import gmail.loganchazdon.dndhelper.ui.character.AllCharactersViewModel
import gmail.loganchazdon.dndhelper.ui.navigation.NavItem
import gmail.loganchazdon.dndhelper.ui.navigation.Navigation
import gmail.loganchazdon.dndhelper.ui.navigation.bottomNavBar.BottomNavigationBar
import gmail.loganchazdon.dndhelper.ui.navigation.sideDrawer.SideNavDrawer

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun RootView(allCharactersViewModel: AllCharactersViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(allCharactersViewModel.getAllCharacters()?.observeAsState()?.value?.size) {
        if(allCharactersViewModel.getAllCharacters()?.value?.size == 0) {
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
                                painter = painterResource(R.drawable.ic_race_icon)
                            ),
                            NavItem(
                                name = "Background",
                                route = "$route/BackgroundView/$id",
                                baseRoute = "$route/BackgroundView",
                                painter = painterResource(R.drawable.ic_background_icon)
                            ),
                            NavItem(
                                name = "Stats",
                                route = "$route/StatsView/$id",
                                baseRoute = "$route/StatsView",
                                painter = painterResource(R.drawable.ic_stats_icon)
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
                                painter = painterResource(R.drawable.ic_stats_icon)
                            ),
                            NavItem(
                                name = "Combat",
                                route = "$route/CombatView/$id",
                                baseRoute = "$route/CombatView",
                                painter = painterResource(R.drawable.ic_combat_icon)
                            ),
                            NavItem(
                                name = "Items",
                                route = "$route/ItemsView/$id",
                                baseRoute = "$route/ItemsView",
                                painter = painterResource(R.drawable.ic_items_icon)
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