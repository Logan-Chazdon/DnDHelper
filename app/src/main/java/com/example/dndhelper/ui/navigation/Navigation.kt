package com.example.dndhelper.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dndhelper.ui.character.*
import com.example.dndhelper.ui.newCharacter.*


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "allCharactersView"){


        composable("allCharactersView") {
            val viewModel = hiltViewModel<AllCharactersViewModel>()
            AllCharactersView(viewModel, navController)
        }


        composable("characterView/MainView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let {
                val viewModel = hiltViewModel<CharacterMainViewModel>()
                CharacterMainView(it, viewModel)
            }
        }

        composable("characterView/AbilitiesView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let {
                AbilitiesView(it)
            }
        }

        composable("characterView/CombatView/{characterId}") { backStackEntry ->
            CombatView(hiltViewModel())
        }

        composable("characterView/ItemsView/{characterId}") {
            ItemsView(hiltViewModel())
        }

        composable("characterView/StatsView/{characterId}") {
            StatsView(hiltViewModel())
        }





        composable("newCharacterView/BackgroundView/{characterId}") { backStackEntry ->
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                    val viewModel = hiltViewModel<NewCharacterBackgroundViewModel>()
                    BackgroundView(characterId = characterId, navController = navController ,viewModel = viewModel)
                }
            }
            composable("newCharacterView/ClassView/{characterId}") { backStackEntry ->
                val characterId= backStackEntry.arguments?.getString("characterId")?.toInt() ?: -1
                val viewModel = hiltViewModel<NewCharacterClassViewModel>()
                ClassView(viewModel, navController = navController, characterId = characterId)

            }
            composable("newCharacterView/ClassView/ConfirmClassView/{classIndex}/{characterId}") { backStackEntry ->
                backStackEntry.arguments?.getString("classIndex")?.toInt()?.let { classIndex ->
                    backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                        val viewModel = hiltViewModel<NewCharacterClassViewModel>()
                        ConfirmClassView(
                            viewModel = viewModel,
                            navController = navController,
                            classIndex = classIndex,
                            characterId = characterId
                        )
                    }
                }
            }
            composable("newCharacterView/BackgroundView/ConfirmBackGroundView/{backgroundIndex}/{characterId}") { backStackEntry ->
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                    val backgroundIndex = backStackEntry.arguments?.getString("backgroundIndex")?.toInt()
                    val viewModel = hiltViewModel<NewCharacterBackgroundViewModel>()
                    ConfirmBackgroundView(
                        characterId = characterId,
                        navController = navController,
                        viewModel = viewModel,
                        backgroundIndex = backgroundIndex ?: 0
                    )
                }
            }
            composable("newCharacterView/ConfirmRaceView/{raceIndex}/{characterId}") { backStackEntry ->
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
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let {characterId ->
                    val viewModel = hiltViewModel<NewCharacterStatsViewModel>()
                    StatsView(viewModel, characterId)
                }
            }
        }
}