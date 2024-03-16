package com.msukno.gameapprawg.ui.navigation.adaptable

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
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.app_settings.NavGraphUiState
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsScreen
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchScreen

@Composable
fun DetailNavGraph(
    settingsViewModel: AppSettingsViewModel,
    detailNavControler: NavHostController,
    layoutType: LayoutType
){
    val navGraphState  = settingsViewModel.navGraphUiState.collectAsState()
    LaunchedEffect(layoutType) { detailNavControler.navigate(EntryPointDestination.route) }
    NavHost(
        navController = detailNavControler,
        startDestination = EntryPointDestination.route,
    ) {
        composable(route = EntryPointDestination.route){
            when(val state = navGraphState.value) {
                is NavGraphUiState.Loading -> LoadingScreen()
                is NavGraphUiState.Complete -> {
                    val details = state.navGraphDetails
                    detailNavControler.navigate(details.startRouteDetail)
                }
            }
        }
        composable(route = GameFavoriteDestination.route){
            GameFavoriteScreen(
                onGameSelect = { detailNavControler.navigate("${GameDetailsDestination.route}/$it") },
                showBackIcon = false
            )
        }

        composable(
            route = GameDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(GameDetailsDestination.gameIdArg) { type = NavType.IntType })
        ){
            GameDetailsScreen(
                showBackButton = false
            )
        }

        composable(
            route = GameSearchDestination.routeWithArgs,
            arguments = listOf(navArgument(GameSearchDestination.genreIdArg) { type = NavType.IntType })
        ){
            GameSearchScreen(
                onGameSelect = { detailNavControler.navigate("${GameDetailsDestination.route}/$it") },
                showBackIcon = false
            )
        }
    }
}