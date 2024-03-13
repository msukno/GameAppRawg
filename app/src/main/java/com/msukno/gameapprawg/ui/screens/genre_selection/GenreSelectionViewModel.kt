package com.msukno.gameapprawg.ui.screens.genre_selection


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.genre.GenreRepository
import com.msukno.gameapprawg.data.genre_image.GenreImageRepository
import com.msukno.gameapprawg.model.Genre
import com.msukno.gameapprawg.model.GenreImage
import com.msukno.gameapprawg.network.RawgDataLoader
import com.msukno.gameapprawg.network.RawgRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



/**
 * ViewModel for the GenreSelection screen. It fetches genres from the local database and the RAWG API,
 * and provides them to the UI for user selection.
 */
class GenreSelectionViewModel(
    genreRepository: GenreRepository,
    genreImageRepository: GenreImageRepository,
    rawgRepository: RawgRepository,
    cacheRepository: ImageCacheRepository
): ViewModel(){

    companion object {
        // Timeout for subscription to the state flow
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // DataLoader for fetching data from the RAWG API
    val networkDataLoader = RawgDataLoader(rawgRepository)

    // Cache for storing paths to genre images
    var imagePathsCache: Map<Int, GenreImage> = mapOf()

    /**
     * StateFlow for managing the UI state of the genre list. It fetches genres sorted by game count from the local database.
     */
    val genreUiState: StateFlow<GenreUiState> =
        genreRepository.getGenreByGamesCount().map { GenreUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = GenreUiState()
        )

    init {
        viewModelScope.launch {
            // Load cache in memory
            genreImageRepository.getAllImagesStream().first().let { images ->
                imagePathsCache = images.associateBy { it.genreId }
            }

            // Fetch all genres from the RAWG API and update cache if new genres are found
            val genres = networkDataLoader.fetchAllGenres(startPage = "1")
            if(genres.isNotEmpty()){
                genreRepository.insert(genres)
                cacheRepository.updateGenreCache()
            }
        }
    }
}

/**
 * Data class for representing the UI state of the genre list.
 */
data class GenreUiState(val genres : List<Genre> = listOf())
