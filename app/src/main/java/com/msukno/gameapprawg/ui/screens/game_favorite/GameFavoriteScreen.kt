package com.msukno.gameapprawg.ui.screens.game_favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.model.toGame
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameCard

object GameFavoriteDestination: NavigationDestination {
    override val route: String = "GameFavorite"
    override val titleResource: Int = R.string.favorites
}
@Composable
fun GameFavoriteScreen(
    viewModel: GameFavoriteViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onGameSelect: (Int) -> Unit = {},
    navigateBack: () -> Unit = {},
    showBackIcon: Boolean = true
){
    val uiState = viewModel.favGamesUiState.collectAsStateWithLifecycle()
    val games = uiState.value.games
    val imgCache = viewModel.imagePathsCache

    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))){
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))

        ){
            if (showBackIcon){
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.favorites),
                style = MaterialTheme.typography.displaySmall
            )
        }
        Row(modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.padding_small),
                end = dimensionResource(id = R.dimen.padding_small),
                bottom = dimensionResource(id = R.dimen.padding_small)
            )
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.line_thick))
                    .background(Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_large)))
        LazyColumn(){
            items(games.size){
                val game = games[it]
                GameCard(
                    game = game.toGame(),
                    imageCache = imgCache,
                    onGameSelect = onGameSelect
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            }
        }
    }
}