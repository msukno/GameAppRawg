package com.msukno.gameapprawg.ui.navigation.adaptable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsScreen
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchScreen

@Composable
fun DetailNavGraph(
    detailNavControler: NavHostController
){
    NavHost(
        navController = detailNavControler,
        startDestination = GameFavoriteDestination.route,
    ) {
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