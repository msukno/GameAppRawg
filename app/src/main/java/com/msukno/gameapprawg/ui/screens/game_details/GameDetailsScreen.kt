package com.msukno.gameapprawg.ui.screens.game_details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.data.database.Converters
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.LoadingScreen
import com.msukno.gameapprawg.ui.screens.game_list.DisplayImageFromStorage
import com.msukno.gameapprawg.ui.screens.game_list.DisplayImageFromWeb
import com.msukno.gameapprawg.ui.screens.game_list.ImageType
import org.jsoup.Jsoup

object GameDetailsDestination: NavigationDestination{
    override val route: String = "GameDetails"
    override val titleResource: Int = 0
    const val gameIdArg = "gameId"
    val routeWithArgs = "$route/{$gameIdArg}"


}
@Composable
fun GameDetailsScreen(
    viewModel: GameDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit = {},
) {
    val uiState = viewModel.uiState
    val imageCache = viewModel.imagePathsCache
    var favorite by remember{ mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { DetailsScreenBotBar(
            favorite,
            buttonEnabled = enabled,
            onClickBack = navigateBack,
            onClickAddOrRemove = { viewModel.updateFavorites() }
        )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ){
            when(uiState){
                GameDetailsUiState.Loading -> LoadingScreen()
                is GameDetailsUiState.Complete -> {
                    favorite = uiState.favorite
                    enabled = true
                    val gameDetails = uiState.gameDetails

                    GameDetailsHeader(gameDetails = gameDetails)
                    if (gameDetails.description.isEmpty()){
                        LoadingScreen()
                    }
                    else {
                        GameDetailsBody(
                            gameDetails = gameDetails,
                            cachedScreenshots = imageCache[gameDetails.id]?.screenshots
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsScreenBotBar(
    favorite: Boolean,
    buttonEnabled: Boolean,
    onClickBack: () -> Unit = {},
    onClickAddOrRemove: () -> Unit = {}
){

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .padding(
                start = dimensionResource(id = R.dimen.padding_extra_small),
                end = dimensionResource(id = R.dimen.padding_extra_small)
            )
    ){

        OutlinedButton(
            onClick = { onClickBack() },
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(id = R.dimen.button_padding_small))
        ) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_extra_small)))

        OutlinedButton(
            onClick = { onClickAddOrRemove() },
            enabled = buttonEnabled,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(id = R.dimen.button_padding_small))
        ) {
            Text(
                text = if (favorite) stringResource(id = R.string.remove_favor)
                else stringResource(id = R.string.add_favor),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun GameDetailsBody(
    gameDetails: GameDetails,
    cachedScreenshots: Set<String>?
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = dimensionResource(id = R.dimen.padding_small))
            .padding(start = dimensionResource(id = R.dimen.padding_extra_small))
    ){

        Text(
            text = Jsoup.parse(gameDetails.description).text(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = dimensionResource(id = R.dimen.padding_small))
            .padding(start = dimensionResource(id = R.dimen.padding_extra_small))
    ) {
        val text1 = stringResource(id = R.string.platforms)
        val text2 = Converters().fromSet2Text(gameDetails.platforms)

        val annotatedString = buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.headlineMedium.toSpanStyle()
                .copy(color = MaterialTheme.colorScheme.onBackground)) {
                append(text1)
            }
            append("  ")
            withStyle(style = MaterialTheme.typography.bodySmall.toSpanStyle()
                .copy(color = MaterialTheme.colorScheme.onBackground)) {
                append(text2)
            }
        }
        Text(
            text = annotatedString,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        )
    }

    Column(){
        cachedScreenshots?.let { screenshots ->
            screenshots.forEach { path ->
                DisplayImageFromStorage(path = path, type = ImageType.screenshot)
            }
        } ?: gameDetails.screenshots.forEach{
            DisplayImageFromWeb(url = it, type = ImageType.screenshot)
        }
    }
}

@Composable
fun GameDetailsHeader(
    gameDetails: GameDetails
){
    Text(
        text = gameDetails.name,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ){
        Box(
            modifier = Modifier
                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .border(1.dp, Color.Green, shape = MaterialTheme.shapes.small)
                .padding(
                    start = dimensionResource(id = R.dimen.padding_small),
                    end = dimensionResource(id = R.dimen.padding_small),
                    bottom = 2.dp
                )
        ) {
            Text(
                text = gameDetails.rating.toString(),
                color = Color.Green,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = gameDetails.ratingsCount.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.padding_large),
                end = dimensionResource(id = R.dimen.padding_extra_small)
            )

        )
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
        )
    }
    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = dimensionResource(id = R.dimen.padding_small))
            .padding(start = dimensionResource(id = R.dimen.padding_extra_small))
    ){
        Text(
            text = "Released:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .weight(1f)
        )

        Text(
            text = gameDetails.released,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }
    Row(modifier = Modifier
        .padding(
            start = dimensionResource(id = R.dimen.padding_small),
            end = dimensionResource(id = R.dimen.padding_small),
            bottom = dimensionResource(id = R.dimen.padding_small)
        )){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.line_thick))
                .background(Color.Gray)
        )
    }
}