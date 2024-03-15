package com.msukno.gameapprawg

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.ui.adaptable_screen.ListDetailView
import com.msukno.gameapprawg.ui.adaptable_screen.ListView
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel

enum class SettingsPosition{
    TopBar, NavigationRail, PermanentNavigationDrawer
}

@Composable
fun GameApp(
    windowSize: WindowWidthSizeClass,
    settingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    val settingsPosition: SettingsPosition = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            SettingsPosition.TopBar
        }
        WindowWidthSizeClass.Medium -> {
            SettingsPosition.NavigationRail
        }
        WindowWidthSizeClass.Expanded -> {
            SettingsPosition.PermanentNavigationDrawer
        }
        else -> {
            SettingsPosition.TopBar
        }
    }

    if(settingsPosition == SettingsPosition.PermanentNavigationDrawer)
        ListDetailView(settingsViewModel, settingsPosition)
    else
        ListView(settingsViewModel, settingsPosition = settingsPosition)
}