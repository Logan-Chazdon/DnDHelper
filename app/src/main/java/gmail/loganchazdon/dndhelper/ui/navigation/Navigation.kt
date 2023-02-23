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
import gmail.loganchazdon.dndhelper.ui.homebrew.*
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
            HomebrewView(navController = navController, viewModel = hiltViewModel())
        }

        composable("homebrewView/homebrewFeature/{featureId}") {
            HomebrewFeatureView(viewModel = hiltViewModel(), navController= navController)
        }

        composable("homebrewView/homebrewRaceView/{raceId}") {
            HomebrewRaceView(navController = navController, viewModel = hiltViewModel())
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
        composable("newCharacterView/ClassView/ConfirmClassView/{classId}/{characterId}") {
            ConfirmClassView(viewModel = hiltViewModel(), navController = navController)
        }

        composable("newCharacterView/BackgroundView/ConfirmBackGroundView/{backgroundId}/{characterId}") {
            ConfirmBackgroundView(
                navController = navController,
                viewModel = hiltViewModel(),
            )
        }
        composable("newCharacterView/RaceView/ConfirmRaceView/{raceId}/{characterId}") { 
            ConfirmRaceView(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable("newCharacterView/RaceView/{characterId}") {
                val viewModel = hiltViewModel<NewCharacterRaceViewModel>()
                RaceView(viewModel, navController = navController)
        }
        composable("newCharacterView/StatsView/{characterId}") {
            val viewModel = hiltViewModel<NewCharacterStatsViewModel>()
            StatsView(viewModel, navController)
        }

        composable("homebrewView/homebrewSubraceView/{id}"){
            HomebrewSubraceView(viewModel = hiltViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewClassView/{id}") {
            HomebrewClassView(viewModel = hiltViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewSubclassView/{id}") {
            HomebrewSubclassView(viewModel = hiltViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewSpellView/{id}") {
            HomebrewSpellView(viewModel = hiltViewModel(), navController = navController)
        }
    }
}