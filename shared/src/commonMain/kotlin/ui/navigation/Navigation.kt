package ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import ui.character.*
import ui.homebrew.*
import ui.newCharacter.*
import ui.preferences.PreferencesView



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
            HomebrewView(navController = navController, viewModel = koinViewModel())
        }

        composable("homebrewView/homebrewFeature/{featureId}") {
            HomebrewFeatureView(viewModel = koinViewModel(), navController= navController)
        }

        composable("homebrewView/homebrewRaceView/{raceId}") {
            HomebrewRaceView(navController = navController, viewModel = koinViewModel())
        }

        composable("allCharactersView") {
            val viewModel = koinViewModel<AllCharactersViewModel>()
            AllCharactersView(viewModel, navController)
        }


        composable("characterView/MainView/{characterId}") {
            val viewModel = koinViewModel<CharacterMainViewModel>()
            CharacterMainView(viewModel)
        }


        composable("characterView/CombatView/{characterId}") { backStackEntry ->
            CombatView(koinViewModel())
        }

        composable("characterView/ItemsView/{characterId}") {
            ItemsView(koinViewModel(), navController)
        }

        composable("characterView/ItemsView/ItemDetailView/{characterId}/{itemIndex}") {
            ItemDetailsView(koinViewModel())
        }

        composable("characterView/StatsView/{characterId}") {
            StatsView(viewModel = koinViewModel())
        }


        composable("newCharacterView/BackgroundView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                BackgroundView(
                    characterId = characterId,
                    navController = navController,
                    viewModel = koinViewModel()
                )
            }
        }
        composable("newCharacterView/ClassView/{characterId}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId")?.toInt() ?: -1
            ClassView(viewModel = koinViewModel(), navController = navController, characterId = characterId)

        }
        composable("newCharacterView/ClassView/ConfirmClassView/{classId}/{characterId}") {
            ConfirmClassView(viewModel = koinViewModel(), navController = navController)
        }

        composable("newCharacterView/BackgroundView/ConfirmBackGroundView/{backgroundId}/{characterId}") {
            ConfirmBackgroundView(
                navController = navController,
                viewModel = koinViewModel(),
            )
        }

        composable("newCharacterView/RaceView/ConfirmRaceView/{raceId}/{characterId}") { 
            ConfirmRaceView(
                viewModel = koinViewModel(),
                navController = navController
            )
        }
        composable("newCharacterView/RaceView/{characterId}") {
                RaceView(viewModel= koinViewModel(), navController = navController)
        }
        composable("newCharacterView/StatsView/{characterId}") {
            StatsView(viewModel= koinViewModel(), navController)
        }

        composable("homebrewView/homebrewSubraceView/{id}"){
            HomebrewSubraceView(viewModel = koinViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewClassView/{id}") {
            HomebrewClassView(viewModel = koinViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewSubclassView/{id}") {
            HomebrewSubclassView(viewModel = koinViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewSpellView/{id}") {
            HomebrewSpellView(viewModel = koinViewModel(), navController = navController)
        }

        composable("homebrewView/homebrewBackgroundView/{id}") {
            HomebrewBackgroundView(viewModel = koinViewModel(), navController = navController)
        }
    }
}