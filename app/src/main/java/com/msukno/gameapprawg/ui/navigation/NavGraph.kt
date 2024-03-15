package com.msukno.gameapprawg.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsScreen
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteScreen
import com.msukno.gameapprawg.ui.screens.game_list.GameListDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchScreen
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionScreen
import com.msukno.gameapprawg.ui.screens.LoadingScreen
import com.msukno.gameapprawg.ui.screens.app_settings.NavGraphUiState


object EntryPointDestination: NavigationDestination{
    override val route: String = "AppEntryPoint"
    override val titleResource: Int = 0
}

@Composable
fun NavGraph(
    settingsViewModel: AppSettingsViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
){
    val navGraphState = settingsViewModel.navGraphUiState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = EntryPointDestination.route,
        modifier = modifier
    ) {
        composable(route = EntryPointDestination.route){
            when(val state = navGraphState.value) {
                is NavGraphUiState.Loading -> LoadingScreen()
                is NavGraphUiState.Complete -> {
                    val details = state.navGraphDetails
                    navController.navigate(details.startRouteCompact)
                }
            }
        }

        composable(route = GenreSelectionDestination.route){
            settingsViewModel.updateRouteList(GenreSelectionDestination.route)
            GenreSelectionScreen(
                onGenreSelect = { genreId, genreName ->
                    settingsViewModel.writeParams(genreId.toString(), genreName)
                    navController.navigate("${GameListDestination.route}/$genreId/$genreName")
                }
            )
        }

        composable(
            route = GameListDestination.routeWithArgs,
            arguments = listOf(
                navArgument(GameListDestination.genreIdArg) { type = NavType.IntType },
                navArgument(GameListDestination.genreNameArg) { type = NavType.StringType }
            )
        ){backStack ->
            val genreId = backStack.arguments?.getInt(GameListDestination.genreIdArg)
            val genreName = backStack.arguments?.getString(GameListDestination.genreNameArg)
            val route = "${GameListDestination.route}/$genreId/$genreName"
            Log.d("NavGraph", "updateNavGraphRouteList: $route")
            settingsViewModel.updateRouteList(route)
            GameListScreen(
                settingsViewModel = settingsViewModel,
                onGameSelect = { navController.navigate("${GameDetailsDestination.route}/$it")},
                navigateToSearch = { navController.navigate("${GameSearchDestination.route}/$it") },
                navigateToFavorites = { navController.navigate(GameFavoriteDestination.route) }
            )
        }
        composable(
            route = GameDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(GameDetailsDestination.gameIdArg) { type = NavType.IntType })
        ){backStack ->
            val gameId = backStack.arguments?.getInt(GameDetailsDestination.gameIdArg)
            val route = "${GameDetailsDestination.route}/$gameId"
            settingsViewModel.updateRouteDetail(route)
            GameDetailsScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = GameFavoriteDestination.route){
            settingsViewModel.updateRouteDetail(GameFavoriteDestination.route)
            GameFavoriteScreen(
                navigateBack = { navController.popBackStack() },
                onGameSelect = { navController.navigate("${GameDetailsDestination.route}/$it") }
            )
        }
        composable(route = AppSettingsDestination.route){
            settingsViewModel.updateRouteList(AppSettingsDestination.route)
            AppSettingsScreen(
                viewModel = settingsViewModel,
                navigateToGenreSelect = { navController.navigate(GenreSelectionDestination.route) },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = GameSearchDestination.routeWithArgs,
            arguments = listOf(navArgument(GameSearchDestination.genreIdArg) { type = NavType.IntType })
        ){backStack ->
            val genreId = backStack.arguments?.getInt(GameSearchDestination.genreIdArg)
            val route = "${GameSearchDestination.route}/$genreId"
            settingsViewModel.updateRouteDetail(route)
            GameSearchScreen(
                onGameSelect = { navController.navigate("${GameDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}