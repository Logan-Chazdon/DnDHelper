package gmail.loganchazdon.dndhelper.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import gmail.loganchazdon.dndhelper.ui.character.*
import gmail.loganchazdon.dndhelper.ui.homebrew.HomebrewView
import gmail.loganchazdon.dndhelper.ui.newCharacter.*
import gmail.loganchazdon.dndhelper.ui.preferences.PreferencesView


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "allCharactersView") {


        composable("preferences") {
            PreferencesView()
        }


        composable("homebrewView") {
            HomebrewView(navController = navController)
        }

        composable("allCharactersView") {
            val viewModel = hiltViewModel<AllCharactersViewModel>()
            AllCharactersView(viewModel, navController)
        }


        composable("characterView/MainView/{characterId}") {
            val viewModel = hiltViewModel<CharacterMainViewModel>()
            CharacterMainView(viewModel)
        }


        composable("characterView/CombatView/{characterId}") { backStackEntry ->
            CombatView(hiltViewModel())
        }

        composable("characterView/ItemsView/{characterId}") {
            ItemsView(hiltViewModel(), navController)
        }

        composable("characterView/ItemsView/ItemDetailView/{characterId}/{itemIndex}") {
            ItemDetailsView(hiltViewModel())
        }

        composable("characterView/StatsView/{characterId}") {
            StatsView(hiltViewModel())
        }





        composable("newCharacterView/BackgroundView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                val viewModel = hiltViewModel<NewCharacterBackgroundViewModel>()
                BackgroundView(
                    characterId = characterId,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        composable("newCharacterView/ClassView/{characterId}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId")?.toInt() ?: -1
            val viewModel = hiltViewModel<NewCharacterClassViewModel>()
            ClassView(viewModel, navController = navController, characterId = characterId)

        }
        composable("newCharacterView/ClassView/ConfirmClassView/{classIndex}/{characterId}") {
            val viewModel = hiltViewModel<NewCharacterClassViewModel>()
            ConfirmClassView(viewModel = viewModel, navController = navController)
        }

        composable("newCharacterView/BackgroundView/ConfirmBackGroundView/{backgroundIndex}/{characterId}") { backStackEntry ->
            val backgroundIndex =
                backStackEntry.arguments?.getString("backgroundIndex")?.toInt()
            val viewModel = hiltViewModel<NewCharacterBackgroundViewModel>()
            ConfirmBackgroundView(
                navController = navController,
                viewModel = viewModel,
                backgroundIndex = backgroundIndex ?: 0
            )
        }
        composable("newCharacterView/RaceView/ConfirmRaceView/{raceIndex}/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("raceIndex")?.toInt()?.let { raceIndex ->
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                    val viewModel = hiltViewModel<NewCharacterRaceViewModel>()
                    ConfirmRaceView(
                        viewModel = viewModel,
                        navController = navController,
                        raceIndex = raceIndex,
                        characterId = characterId
                    )
                }
            }
        }
        composable("newCharacterView/RaceView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt().let { characterId ->
                val viewModel = hiltViewModel<NewCharacterRaceViewModel>()
                RaceView(viewModel, navController = navController, characterId = characterId ?: -1)
            }
        }
        composable("newCharacterView/StatsView/{characterId}") { backStackEntry ->
            val viewModel = hiltViewModel<NewCharacterStatsViewModel>()
            StatsView(viewModel, navController)
        }
    }
}