package com.msukno.gameapprawg.ui.navigation.adaptable

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.msukno.gameapprawg.LayoutType
import com.msukno.gameapprawg.ui.navigation.EntryPointDestination
import com.msukno.gameapprawg.ui.screens.LoadingScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.app_settings.InitRouteUiState
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionScreen

@Composable
fun ListNavGraph(
    settingsViewModel: AppSettingsViewModel,
    listNavController: NavHostController,
    detailNavController: NavHostController,
    layoutType: LayoutType
){
    val initRouteState = settingsViewModel.initRouteUiState
    LaunchedEffect(layoutType) { listNavController.navigate(EntryPointDestination.route) }

    NavHost(
        navController = listNavController,
        startDestination = EntryPointDestination.route,
    ) {

        composable(route = EntryPointDestination.route){
            when(val state = initRouteState) {
                is InitRouteUiState.Loading -> LoadingScreen()
                is InitRouteUiState.Complete -> {
                    listNavController.navigate(
                        settingsViewModel.currentRoute.currentRouteList ?: state.initialRoute
                    )
                }
            }
        }

        composable(route = GenreSelectionDestination.route){
            settingsViewModel.updateRouteCompact(GenreSelectionDestination.route)
            GenreSelectionScreen(
                onGenreSelect = { genreId, genreName ->
                    settingsViewModel.writeParams(genreId.toString(), genreName)
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
        ){ entry ->
            val genreId = entry.arguments?.getInt(GameListDestination.genreIdArg)
            val genreName = entry.arguments?.getString(GameListDestination.genreNameArg)
            val route = "${GameListDestination.route}/$genreId/$genreName"
            settingsViewModel.updateRouteCompact(route)
            GameListScreen(
                settingsViewModel = settingsViewModel,
                onGameSelect = { detailNavController.navigate("${GameDetailsDestination.route}/$it")},
                navigateToSearch = { detailNavController.navigate("${GameSearchDestination.route}/$it") },
                navigateToFavorites = { detailNavController.navigate(GameFavoriteDestination.route) }
            )
        }

        composable(route = AppSettingsDestination.route){
            settingsViewModel.updateRouteCompact(AppSettingsDestination.route)
            AppSettingsScreen(
                viewModel = settingsViewModel,
                navigateToGenreSelect = { listNavController.navigate(GenreSelectionDestination.route) },
                navigateBack = { listNavController.popBackStack() }
            )
        }
    }
}