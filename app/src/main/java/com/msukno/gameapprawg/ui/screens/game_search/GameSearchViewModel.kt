package com.msukno.gameapprawg.ui.screens.game_search
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.network.RawgDataLoader
import com.msukno.gameapprawg.network.RawgRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 *ViewModel responsible for managing the state and data for the GameSearch screen.
 */
class GameSearchViewModel(
    savedStateHandle: SavedStateHandle,
    rawgRepository: RawgRepository
): ViewModel(){

    // DataLoader instance for fetching data from the RAWG API
    private val dataLoader = RawgDataLoader(rawgRepository)
    private val _uiState: MutableStateFlow<GameSearchUiState> = MutableStateFlow(GameSearchUiState.Default())
    val uiState: StateFlow<GameSearchUiState> = _uiState
    // Fetch the ID of the genre selected by the user
    val genreId: Int = checkNotNull(savedStateHandle[GameSearchDestination.genreIdArg])

    /**
     * Updates the UI state with the new search results.
     */
    fun updateUiState(newSearchResult: List<Game>){
        _uiState.update {
            GameSearchUiState.Default(newSearchResult)
        }
    }

    private fun setUiStateSearching(){
        _uiState.update {
            GameSearchUiState.Searching
        }
    }

    /**
     * Performs a search using the provided search query.
     */
    fun performSearch(searchQuery: String){
        viewModelScope.launch {
            setUiStateSearching()
            val result = dataLoader.performSearch(genreId, searchQuery)
            updateUiState(result)
        }
    }
}

// Holds the state of the UI, which includes the search results.
sealed interface GameSearchUiState {
    object Searching : GameSearchUiState
    data class Default(val searchResult: List<Game> = listOf()) : GameSearchUiState
}