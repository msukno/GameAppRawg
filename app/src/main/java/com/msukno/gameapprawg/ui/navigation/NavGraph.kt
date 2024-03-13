package com.msukno.gameapprawg.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.ui.screens.app_settings.AppParamsUiState
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel.Companion.GENRE_ID_KEY
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel.Companion.GENRE_NAME_KEY
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


object EntryPointDestination: NavigationDestination{
    override val route: String = "AppEntryPoint"
    override val titleResource: Int = 0
}

@Composable
fun NavGraph(
    navController: NavHostController,
    settingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = EntryPointDestination.route,
        modifier = modifier
    ) {
        composable(route = EntryPointDestination.route){
            when(val state = settingsViewModel.paramsState) {
                AppParamsUiState.Loading -> LoadingScreen()
                is AppParamsUiState.Complete -> {
                    val params = state.params
                    val idKey: String = checkNotNull(params[GENRE_ID_KEY])
                    val nameKey: String = checkNotNull(params[GENRE_NAME_KEY])
                    try {
                        val genreId = idKey.toInt()
                        navController.navigate("${GameListDestination.route}/$genreId/$nameKey")
                    }catch (throwable: Throwable){
                        Log.d("EntryPointDestination", "Cant convert genreId to integer; go to genre selection ")
                        navController.navigate(GenreSelectionDestination.route)
                    }
                }
            }
        }

        composable(route = GenreSelectionDestination.route){
            Log.d("NavGraph", "cache state status: ${settingsViewModel.cacheState}")
            GenreSelectionScreen(
                onGenreSelect = { genreId, genreName ->
                    settingsViewModel.updateParams(genreId.toString(), genreName)
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
        ){
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
        ){
            GameDetailsScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = GameFavoriteDestination.route){
            GameFavoriteScreen(
                navigateBack = { navController.popBackStack() },
                onGameSelect = { navController.navigate("${GameDetailsDestination.route}/$it") }
            )
        }
        composable(route = AppSettingsDestination.route){
            AppSettingsScreen(
                viewModel = settingsViewModel,
                navigateToGenreSelect = { navController.navigate(GenreSelectionDestination.route) },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = GameSearchDestination.routeWithArgs,
            arguments = listOf(navArgument(GameSearchDestination.genreIdArg) { type = NavType.IntType })
        ){
            GameSearchScreen(
                onGameSelect = { navController.navigate("${GameDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}