package com.msukno.gameapprawg.ui.adaptable_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.msukno.gameapprawg.GameAppBar
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.ui.navigation.NavGraph
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination

@Composable
fun ListView(
    navController: NavHostController = rememberNavController(),
    settingsPosition: SettingsPosition
){
    Box{
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = settingsPosition == SettingsPosition.NavigationRail) {
                NavigationRail(modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.padding_extra_small))
                ){
                    NavigationRailItem(
                        selected = false,
                        onClick = {
                            navController.navigate( AppSettingsDestination.route )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
                            )
                        }
                    )
                }
            }
            Scaffold(
                topBar = { GameAppBar(
                    containsSettings = settingsPosition == SettingsPosition.TopBar,
                    navigateToSettings = { navController.navigate(AppSettingsDestination.route) })
                }
            ){ innerPadding ->
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}