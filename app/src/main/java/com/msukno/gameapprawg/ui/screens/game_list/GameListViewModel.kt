package com.msukno.gameapprawg.ui.screens.game_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.network.RawgRepository
import com.msukno.gameapprawg.utils.HelperFunctions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


// Pagination params
private const val START_PAGE_KEY = "1"
private const val PAGE_SIZE = 12

// Sort types
enum class GameSortKey(var value: String) {
    ratingDESC(value = "-rating"),
    releasedDESC(value = "-released"),
}

// Cache image types
enum class ImageType{
    screenshot,
    background
}

/**
 * ViewModel for the GameList screen. It fetches games from the local database and provides them
 * to the UI for user selection. The games can be sorted by rating and release date.
 */
class GameListViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val rawgRepository: RawgRepository,
    private val gameImageRepository: GameImageRepository,
    private val cacheRepository: ImageCacheRepository
): ViewModel() {

    // In-memory cache
    var imagePathsCache: Map<Int, GameImages> = mapOf()

    // Fetch the ID and name of the genre selected by the user
    private val genreId: Int = checkNotNull(savedStateHandle[GameListDestination.genreIdArg])
    val genreName : String = checkNotNull(savedStateHandle[GameListDestination.genreNameArg])

    // The initial UI state for pagination
    var uiState by mutableStateOf(
        GameListUiState(
            gameRepository = gameRepository,
            rawgRepository = rawgRepository,
            cacheRepository = cacheRepository,
            scope = viewModelScope,
            params = GameListParams(
                startKey = START_PAGE_KEY,
                pageSize = PAGE_SIZE,
                genreId = genreId,
                validityPeriod = HelperFunctions.dateTimeByThirdOfDay(),
                sortKeys = listOf(GameSortKey.ratingDESC)
            )
        )
    )
        private set

    init {
        viewModelScope.launch {
            // Fetch all game image paths and load them in memory
            gameImageRepository.getAllImagesStream().first().let{ imagesList ->
                imagePathsCache = imagesList.associateBy { it.gameId }
            }
        }
    }

    /**
     * Updates the UI state with the new parameters for fetching and sorting games.
     */
    fun updateUiState(params: GameListParams){
        uiState = GameListUiState(
            gameRepository = gameRepository,
            rawgRepository = rawgRepository,
            cacheRepository = cacheRepository,
            scope = viewModelScope,
            params = params
        )
    }

    /**
     * Clears the games in the local database.
     */
    fun clearCache(){
        viewModelScope.launch {
            gameRepository.clearAll()
        }
    }
}
