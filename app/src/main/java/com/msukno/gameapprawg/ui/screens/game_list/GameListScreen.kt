package com.msukno.gameapprawg.ui.screens.game_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.app_settings.AppCacheUiState
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.common.GameSortKey
import com.msukno.gameapprawg.ui.common.ImageType


object GameListDestination: NavigationDestination {
    override val route: String = "GameList"
    override val titleResource: Int = R.string.game_list
    const val genreIdArg = "genreId"
    const val genreNameArg = "genreName"
    val routeWithArgs = "$route/{$genreIdArg}/{$genreNameArg}"
}


@Composable
fun GameListScreen(
    viewModel: GameListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: AppSettingsViewModel,
    onGameSelect: (Int) -> Unit = {},
    navigateToSearch: (Int) -> Unit = {},
    navigateToFavorites: () -> Unit = {}
){
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val genreName = viewModel.genreName
    val pagingItems = uiState.value.gameList.collectAsLazyPagingItems()
    val imageCache = viewModel.imagePathsCache
    val params = uiState.value.params

    if (settingsViewModel.cacheState is AppCacheUiState.Cleared){
        settingsViewModel.cacheState = AppCacheUiState.Default
        viewModel.updateUiState(params.copy())
    }

    GameListBody(

        games = pagingItems,
        genreId = params.genreId,
        genre = genreName,
        imageCache = imageCache,
        initSortKeys = params.sortKeys,
        onGameSelect = onGameSelect,
        onClickFavorite = navigateToFavorites,
        onClickSearch = navigateToSearch,
        applySort = {
            if (it.isNotEmpty()){
                viewModel.updateUiState(params.copy(sortKeys = it))
            }
        }
    )
}

@Composable
fun GameListBody(
    games: LazyPagingItems<Game>,
    genreId: Int,
    genre: String = "",
    imageCache: Map<Int, GameImages>,
    initSortKeys: List<GameSortKey> = listOf(),
    onGameSelect: (Int) -> Unit = {},
    onClickFavorite: () -> Unit = {},
    onClickSearch: (Int) -> Unit = {},
    applySort: ( List<GameSortKey> ) -> Unit = {}
){

    var showSortOptions by remember{ mutableStateOf(false) }

    Scaffold(
        bottomBar = { GameListBotBar(onClickFavorite = onClickFavorite) }
    ){innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(dimensionResource(id = R.dimen.padding_extra_small))) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_small))
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                        .weight(1f)
                        .padding(start = dimensionResource(id = R.dimen.padding_extra_small))
                        .clickable { onClickSearch(genreId) }
                ){
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
                    )
                }

                IconButton(
                    onClick = { showSortOptions = !showSortOptions },
                    modifier = Modifier
                        .weight(0.2f)
                        .size(dimensionResource(id = R.dimen.icon_size))
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            }

            if (showSortOptions){
                SortOptionsMenu(
                    initSortKeys = initSortKeys,
                    applySort = applySort
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(dimensionResource(id = R.dimen.line_thick))
                        .background(Color.Gray)
                )
                Text(
                    text = genre+ " " + stringResource(GameListDestination.titleResource),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(4f)
                )
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .height(dimensionResource(id = R.dimen.line_thick))
                        .background(Color.Gray)
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))
            LazyColumn(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            ){
                items(games.itemCount){
                    val game = games[it]
                    if (game != null)
                        GameCard(
                            game = game,
                            imageCache = imageCache,
                            onGameSelect = onGameSelect
                        )
                    else Text(text = "Loading...")
                    Spacer(
                        modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    game: Game,
    imageCache: Map<Int, GameImages> = mapOf(),
    onGameSelect: (Int) -> Unit = {}
) {
    val imgPath = imageCache[game.id]?.background
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onGameSelect(game.id) }
    ) {
        Column {
            if (imgPath != null) DisplayImageFromStorage(path = imgPath)
            else DisplayImageFromWeb(url = game.backgroundImage)
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(dimensionResource(id = R.dimen.padding_extra_small))
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(id = R.dimen.padding_extra_small),
                            end = dimensionResource(id = R.dimen.padding_extra_small)
                        )

                ) {

                    Text(
                        text = game.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .weight(2f)
                            .padding(end = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                                .border(
                                    dimensionResource(id = R.dimen.line_thick),
                                    Color.Green,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(start = 6.dp, end = 6.dp, bottom = 1.dp)
                        ) {
                            Text(
                                text = "%.1f".format(game.rating),
                                color = Color.Green,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = game.ratingsCount.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .padding(
                                        start = dimensionResource(id = R.dimen.padding_extra_small),
                                        end = dimensionResource(id = R.dimen.padding_extra_small)
                                    )

                            )
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.line_thick))
                        .background(Color.Gray)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_small))

                ) {
                    Text(
                        text = stringResource(id = R.string.release_date),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = game.released ?: "",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun SortOptionsMenu(
    initSortKeys: List<GameSortKey> = listOf(),
    applySort: ( List<GameSortKey> ) -> Unit = {}
){
    val keyList = initSortKeys.toMutableList()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_small))
    ) {
        FilterChipWrapper(
            sortKeyCollection = keyList,
            sortKey = GameSortKey.RatingDESC,
            name = stringResource(id = R.string.rating)
        )
        FilterChipWrapper(
            sortKeyCollection = keyList,
            sortKey = GameSortKey.ReleasedDESC,
            name = stringResource(id = R.string.releasedDesc)
        )

        Spacer(modifier = Modifier.weight(1f, fill = true))
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = { applySort(keyList.toList()) })
                .background(MaterialTheme.colorScheme.primary)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_small),
                    vertical = dimensionResource(id = R.dimen.padding_extra_small)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.apply),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipWrapper(
    sortKeyCollection: MutableList<GameSortKey>,
    sortKey: GameSortKey,
    name: String
){
    var stateSelected by remember { mutableStateOf(sortKey in sortKeyCollection) }

    FilterChip(
        modifier = Modifier.padding(2.dp),
        selected = stateSelected,
        onClick = {
            stateSelected = !stateSelected
            if(stateSelected) sortKeyCollection.add(sortKey)
            else sortKeyCollection.remove(sortKey)
        },
        label = { Text(
            text = name,
            style = MaterialTheme.typography.labelSmall) },
        leadingIcon = if (stateSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Selected icon",
                    modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small))
                )
            }
        } else {
            null
        }
    )
}

@Composable
fun GameListBotBar(
    onClickFavorite: () -> Unit = {}
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(
                bottom = dimensionResource(id = R.dimen.padding_extra_small),
                start = dimensionResource(id = R.dimen.padding_large),
                end = dimensionResource(id = R.dimen.padding_large))
    ){
        OutlinedButton(
            onClick = { onClickFavorite() },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
        ) {
            Text(
                text = stringResource(id = R.string.favorites),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun DisplayImageFromWeb(url: String, type: ImageType = ImageType.Background) {
    val modifier = Modifier.fillMaxWidth()
    val finalModifier = if (type == ImageType.Background)
        modifier.height(dimensionResource(id = R.dimen.back_image_height))
    else modifier.height(dimensionResource(id = R.dimen.screen_image_height))

    var isLoading by remember { mutableStateOf(false) }
    if (isLoading){
        Image(
            painter = painterResource(id = R.drawable.image_loading),
            contentDescription = "Image not loaded icon",
            contentScale = ContentScale.Crop,
            modifier = finalModifier
        )
    }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Game image from web",
        contentScale = ContentScale.Crop,
        modifier = finalModifier,
        onLoading = { isLoading = true },
        onSuccess = { isLoading = false },
        onError = { isLoading = true }
    )
}

@Composable
fun DisplayImageFromStorage(path: String, type: ImageType = ImageType.Background) {
    val modifier = Modifier.fillMaxWidth()
    val finalModifier = if (type == ImageType.Background)
        modifier.height(dimensionResource(id = R.dimen.back_image_height))
    else modifier.height(dimensionResource(id = R.dimen.screen_image_height))
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(path)
                .crossfade(true)
                .build()
        ),
        contentDescription = "Game image from internal storage",
        contentScale = ContentScale.Crop,
        modifier = finalModifier
    )
}
@Preview
@Composable
fun GameCardPreview(){
}