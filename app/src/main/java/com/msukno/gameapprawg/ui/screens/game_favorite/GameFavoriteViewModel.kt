package com.msukno.gameapprawg.ui.screens.game_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.model.GameFavorite
import com.msukno.gameapprawg.model.GameImages
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the GameFavorite screen. It fetches favorite games from the local database,
 * and provides them to the UI for user viewing. The ViewModel also manages the UI state of the favorite games.
 */
class GameFavoriteViewModel(
    gameFavoriteRepository: GameFavoriteRepository,
    gameImageRepository: GameImageRepository
) : ViewModel() {
    companion object {
        // Timeout for subscription to the StateFlow
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var imagePathsCache: Map<Int, GameImages> = mapOf()

    /**
     * UI state for the favorite games. It fetches all favorite games from the local database and
     * maps them to the UI state.
     */
    val favGamesUiState: StateFlow<GameFavoriteUiState> = gameFavoriteRepository.getAllGamesStream()
        .map { GameFavoriteUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = GameFavoriteUiState()
        )

    init {
        viewModelScope.launch {
            // Fetch all game image paths and load them in memory
            gameImageRepository.getAllImagesStream().first().let{ imagesList ->
                imagePathsCache = imagesList.associateBy { it.gameId }
            }
        }
    }
}

/**
 * UI state for the favorite games. It holds a list of favorite games to be displayed on the UI.
 */
class GameFavoriteUiState(val games: List<GameFavorite> = listOf())