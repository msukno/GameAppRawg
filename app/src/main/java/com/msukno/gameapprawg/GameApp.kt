package com.msukno.gameapprawg

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.msukno.gameapprawg.ui.navigation.NavGraph
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsDestination
import com.msukno.gameapprawg.ui.theme.GameAppRawgTheme

@Composable
fun GameApp(
    navController: NavHostController = rememberNavController()
){
    Scaffold(
        topBar = { GameAppBar(
            navigateToSettings = { navController.navigate(AppSettingsDestination.route) },
        ) }
    ){ innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
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
                        onClick = { navigateToSettings() },
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
    )
}


@Preview
@Composable
fun GameAppPreview(){
    GameAppRawgTheme(darkTheme = true) {
        GameAppBar()
    }
}