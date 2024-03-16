package com.msukno.gameapprawg.ui.screens.app_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.ui.navigation.NavigationDestination


object AppSettingsDestination: NavigationDestination{
    override val route: String = "AppSettings"
    override val titleResource: Int = R.string.settings

}
@Composable
fun AppSettingsScreen(
    viewModel: AppSettingsViewModel,
    navigateToGenreSelect: () -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    var showCacheSettings by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ){
        Row(verticalAlignment = Alignment.CenterVertically){

            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = ""
                )
            }

            Text(
                text = stringResource(id = AppSettingsDestination.titleResource),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.line_thick))
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clickable { navigateToGenreSelect() }
        ){
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            Text(
                text = "Genre selection",
                style = MaterialTheme.typography.headlineMedium
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clickable { showCacheSettings = !showCacheSettings }
        ){
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            Text(
                text = "Manage cache",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        if (showCacheSettings){
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_large),
                        end = dimensionResource(id = R.dimen.padding_small)
                    )
                    .fillMaxWidth()
            ){
                Button(onClick = { viewModel.clearCache() }) {
                    Text(
                        text = stringResource(id = R.string.clear_cache),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}