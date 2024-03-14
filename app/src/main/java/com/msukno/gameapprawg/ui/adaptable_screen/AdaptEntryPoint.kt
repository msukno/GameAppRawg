package com.msukno.gameapprawg.ui.adaptable_screen

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.LoadingScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppParamsUiState
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.game_list.GameListDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionDestination

enum class SettingsPosition{
    TopBar, NavigationRail, PermanentNavigationDrawer
}

@Composable
fun AdaptEntryPoint(
    windowSize: WindowWidthSizeClass,
    settingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val settingsPosition: SettingsPosition

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            settingsPosition = SettingsPosition.TopBar
        }
        WindowWidthSizeClass.Medium -> {
            settingsPosition = SettingsPosition.NavigationRail
        }

        WindowWidthSizeClass.Expanded -> {
            settingsPosition = SettingsPosition.PermanentNavigationDrawer
        }

        else -> {
            settingsPosition = SettingsPosition.TopBar
        }
    }

    when(val state = settingsViewModel.paramsState) {
        AppParamsUiState.Loading -> LoadingScreen()
        is AppParamsUiState.Complete -> {
            val params = state.params
            val idKey: String = checkNotNull(params[AppSettingsViewModel.GENRE_ID_KEY])
            val nameKey: String = checkNotNull(params[AppSettingsViewModel.GENRE_NAME_KEY])
            val startDestination = try {
                val genreId = idKey.toInt()
                "${GameListDestination.route}/$genreId/$nameKey"
            }catch (throwable: Throwable){
                Log.d("EntryPointDestination", "Cant convert genreId to integer; go to genre selection ")
                GenreSelectionDestination.route
            }
            if(windowSize == WindowWidthSizeClass.Expanded)
                ListDetailView(startDestination)
            else
                ListView(settingsPosition = settingsPosition)
        }
    }
}