package com.msukno.gameapprawg.ui.adaptable_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msukno.gameapprawg.AppBarContent
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.ui.navigation.EntryPointDestination
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.navigation.adaptable.DetailNavGraph
import com.msukno.gameapprawg.ui.navigation.adaptable.ListNavGraph
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsScreen
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsDestination
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListScreen
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchDestination

@Composable
fun ListDetailView(
    listStartDestination: String,
    detailNavController: NavHostController = rememberNavController()
){
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(Modifier.width(300.dp)) {
                NavigationDrawerContent(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    ) {
        Log.d("ListDetailView", "StartDestination: $listStartDestination")
        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                ListNavGraph(
                    startDestination = listStartDestination ,
                    detailNavController = detailNavController
                )
            }
            Column(Modifier.weight(1f)) {
                DetailNavGraph(detailNavControler = detailNavController)
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(modifier: Modifier){
    Column(modifier = modifier){
        AppBarContent(containsSettings = false)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            IconButton(
                onClick = {  },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
             Text(
                 text = stringResource(id = R.string.settings),
                 style = MaterialTheme.typography.headlineMedium)
        }
    }
}