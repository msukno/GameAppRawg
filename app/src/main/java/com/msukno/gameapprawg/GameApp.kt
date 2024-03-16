package com.msukno.gameapprawg

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.ui.adaptable_screen.ListDetailView
import com.msukno.gameapprawg.ui.adaptable_screen.ListView
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel

enum class LayoutType{
    TopBar, NavigationRail, PermanentNavigationDrawer
}

@Composable
fun GameApp(
    windowSize: WindowWidthSizeClass,
    settingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    val layoutType: LayoutType = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            LayoutType.TopBar
        }
        WindowWidthSizeClass.Medium -> {
            LayoutType.NavigationRail
        }
        WindowWidthSizeClass.Expanded -> {
            LayoutType.PermanentNavigationDrawer
        }
        else -> {
            LayoutType.TopBar
        }
    }

    if(layoutType == LayoutType.PermanentNavigationDrawer)
        ListDetailView(settingsViewModel, layoutType)
    else
        ListView(settingsViewModel, layoutType)
}