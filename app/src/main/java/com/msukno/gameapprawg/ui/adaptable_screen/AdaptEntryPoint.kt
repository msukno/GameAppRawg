package com.msukno.gameapprawg.ui.adaptable_screen

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

enum class SettingsPosition{
    TopBar, NavigationRail, PermanentNavigationDrawer
}

@Composable
fun AdaptEntryPoint(
    windowSize: WindowWidthSizeClass
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

    ListScreen(settingsPosition = settingsPosition)
}