package com.msukno.gameapprawg.ui.adaptable_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.SettingsPosition
import com.msukno.gameapprawg.ui.navigation.NavGraph
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.theme.GameAppRawgTheme

@Composable
fun ListView(
    settingsViewModel: AppSettingsViewModel,
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
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameAppBar(
    containsSettings: Boolean = true,
    navigateToSettings: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = {
            AppBarContent(containsSettings, navigateToSettings)
        }
    )
}

@Composable
fun AppBarContent(
    containsSettings: Boolean,
    onClickSettings: () -> Unit = {},
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_large))
                .padding(dimensionResource(id = R.dimen.padding_small)),
            painter = painterResource(R.drawable.warlord_helmet_trans),

            contentDescription = null
        )
        val text1 = stringResource(R.string.app_title1)
        val text2 = stringResource(R.string.app_title2)
        val text3 = stringResource(R.string.app_title3)

        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle()) {
                append(text1)
            }
            withStyle(style = SpanStyle().copy(color = MaterialTheme.colorScheme.surfaceTint)) {
                append(text2)
            }
            withStyle(style = SpanStyle()) {
                append(text3)
            }
        }
        Text(
            text = annotatedString,
            //text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .weight(5f)
                .padding(4.dp)
        )
        //Spacer(modifier = Modifier.width(20.dp))
        if (containsSettings){
            IconButton(
                onClick = { onClickSettings() },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun GameAppPreview(){
    GameAppRawgTheme(darkTheme = true) {
        GameAppBar()
    }
}