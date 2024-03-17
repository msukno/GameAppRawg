package com.msukno.gameapprawg.ui.screens.game_search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.LoadingScreen
import com.msukno.gameapprawg.ui.screens.game_list.GameCard
import kotlinx.coroutines.delay
object GameSearchDestination: NavigationDestination{
    override val route: String = "GameSearch"
    override val titleResource: Int = 0
    const val genreIdArg = "genreId"
    val routeWithArgs = "$route/{$genreIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GameSearchScreen(
    viewModel: GameSearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onGameSelect: (Int) -> Unit = {},
    navigateBack: () -> Unit = {},
    showBackIcon: Boolean = true
){

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var inputQuery by remember{ mutableStateOf("") }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }

    Column(
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            if (showBackIcon){
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.search),
                style = MaterialTheme.typography.displaySmall
            )
        }
        SearchBar(
            query = inputQuery,
            onQueryChange = { inputQuery = it },
            onSearch = { viewModel.performSearch(inputQuery) },
            active = true,
            onActiveChange = {},
            content = {
                when(val stateValue = uiState.value){
                    is GameSearchUiState.Searching ->{
                        LoadingScreen()
                    }
                    is GameSearchUiState.Default -> {
                        val gameList = stateValue.searchResult
                        LazyColumn(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_small))) {
                            items(gameList.size) {
                                val game = gameList[it]
                                GameCard(
                                    game = game,
                                    onGameSelect = onGameSelect
                                )
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small))
                .focusRequester(focusRequester)
        )
    }
}

@Preview
@Composable
fun GameSearchScreenPreview(){
    GameSearchScreen()
}
