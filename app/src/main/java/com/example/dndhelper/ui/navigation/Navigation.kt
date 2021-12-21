package com.example.dndhelper.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dndhelper.ui.character.*
import com.example.dndhelper.ui.newCharacter.*


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
                CharacterMainView(it)
            }
        }

        composable("characterView/AbilitiesView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let {
                AbilitiesView(it)
            }
        }

        composable("characterView/CombatView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let {
                CombatView(it)
            }
        }

        composable("characterView/ItemsView/{characterId}") { backStackEntry ->
            backStackEntry.arguments?.getString("characterId")?.toInt()?.let {
                ItemsView(it)
            }
        }





        composable("newCharacterView/BackgroundView/{characterId}") { backStackEntry ->
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                    BackgroundView(characterId = characterId)
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
                            classIndex = classIndex,
                            characterId = characterId
                        )
                    }
                }
            }
            composable("newCharacterView/ConfirmBackGroundView/{characterId}") { backStackEntry ->
                backStackEntry.arguments?.getString("characterId")?.toInt()?.let { characterId ->
                    ConfirmBackgroundView(characterId = characterId)
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