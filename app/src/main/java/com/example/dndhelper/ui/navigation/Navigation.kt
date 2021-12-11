package com.example.dndhelper.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dndhelper.ui.character.AllCharactersView
import com.example.dndhelper.ui.character.AllCharactersViewModel
import com.example.dndhelper.ui.newCharacter.*


@ExperimentalMaterialApi
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "allCharactersView"){


        composable("allCharactersView") {
            val viewModel = hiltViewModel<AllCharactersViewModel>()
            AllCharactersView(viewModel)
        }



        composable("newCharacterView/BackgroundView") {
            BackgroundView()
        }
        composable("newCharacterView/ClassView") {
            val viewModel = hiltViewModel<NewCharacterViewModel>()
            ClassView(viewModel, navController = navController)
        }
        composable("newCharacterView/ClassView/ConfirmClassView/{classIndex}") {backStackEntry ->
            backStackEntry.arguments?.getString("classIndex")?.toInt()?.let {
                ConfirmClassView(
                    viewModel = hiltViewModel<NewCharacterViewModel>(),
                    classIndex = it
                )
            }
        }
        composable("newCharacterView/ConfirmBackGroundView") {
            ConfirmBackgroundView()
        }
        composable("newCharacterView/ConfirmRaceView/{raceIndex}") {backStackEntry ->
            backStackEntry.arguments?.getString("raceIndex")?.toInt()?.let {
                ConfirmRaceView(
                    viewModel = hiltViewModel<NewCharacterViewModel>(),
                    raceIndex = it
                )
            }
        }
        composable("newCharacterView/RaceView") {
            val viewModel = hiltViewModel<NewCharacterViewModel>()
            RaceView(viewModel, navController = navController)
        }
        composable("newCharacterView/statsView") {
            val viewModel = hiltViewModel<NewCharacterViewModel>()
            StatsView(viewModel)
        }
    }
}