package com.msukno.gameapprawg.ui.navigation.adaptable

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.ui.navigation.EntryPointDestination
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionScreen

@Composable
fun ListNavGraph(
    startDestination: String,
    detailNavController: NavHostController,
    listNavController: NavHostController = rememberNavController(),
    settingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    NavHost(
        navController = listNavController,
        startDestination = EntryPointDestination.route,
    ) {
        composable(route = EntryPointDestination.route){
            listNavController.navigate(startDestination)
        }
        composable(route = GenreSelectionDestination.route){
            GenreSelectionScreen(
                onGenreSelect = { genreId, genreName ->
                    settingsViewModel.updateParams(genreId.toString(), genreName)
                    listNavController.navigate("${GameListDestination.route}/$genreId/$genreName")
                }
            )
        }

        composable(
            route = GameListDestination.routeWithArgs,
            arguments = listOf(
                navArgument(GameListDestination.genreIdArg) { type = NavType.IntType },
                navArgument(GameListDestination.genreNameArg) { type = NavType.StringType }
            )
        ){
            GameListScreen(
                settingsViewModel = settingsViewModel,
                onGameSelect = { detailNavController.navigate("${GameDetailsDestination.route}/$it")},
                navigateToSearch = { detailNavController.navigate("${GameSearchDestination.route}/$it") },
                navigateToFavorites = { detailNavController.navigate(GameFavoriteDestination.route) }
            )
        }

        composable(route = AppSettingsDestination.route){
            AppSettingsScreen(
                viewModel = settingsViewModel,
                navigateToGenreSelect = { listNavController.navigate(GenreSelectionDestination.route) },
                navigateBack = { listNavController.popBackStack() }
            )
        }
    }
}