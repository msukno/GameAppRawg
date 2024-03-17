package com.msukno.gameapprawg.ui.adaptable_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.msukno.gameapprawg.LayoutType
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.ui.navigation.adaptable.DetailNavGraph
import com.msukno.gameapprawg.ui.navigation.adaptable.ListNavGraph
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel

@Composable
fun ListDetailView(
    settingsViewModel: AppSettingsViewModel,
    layoutType: LayoutType,
    listNavController: NavHostController = rememberNavController(),
    detailNavController: NavHostController = rememberNavController()
){
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(Modifier.width(dimensionResource(id = R.dimen.perma_draw_width))) {
                NavigationDrawerContent(
                    listNavController,
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    ) {
        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                ListNavGraph(
                    settingsViewModel = settingsViewModel,
                    listNavController = listNavController,
                    detailNavController = detailNavController,
                    layoutType = layoutType
                )
            }
            Column(Modifier.weight(1f)) {
                DetailNavGraph(
                    settingsViewModel = settingsViewModel,
                    detailNavControler = detailNavController,
                    layoutType = layoutType
                )
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(
    navControler: NavHostController,
    modifier: Modifier
){
    Column(modifier = modifier
    ){
        AppBarContent(containsSettings = false)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navControler.navigate(AppSettingsDestination.route) }
        ){
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
                )
            }
             Text(
                 text = stringResource(id = R.string.settings),
                 style = MaterialTheme.typography.headlineMedium)
        }
    }
}