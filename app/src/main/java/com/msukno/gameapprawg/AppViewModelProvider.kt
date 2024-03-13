package com.msukno.gameapprawg

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.msukno.gameapprawg.ui.screens.app_settings.AppSettingsViewModel
import com.msukno.gameapprawg.ui.screens.game_details.GameDetailsViewModel
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteViewModel
import com.msukno.gameapprawg.ui.screens.game_list.GameListViewModel
import com.msukno.gameapprawg.ui.screens.game_search.GameSearchViewModel
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for GenreSelectionViewModel
        initializer {
            GenreSelectionViewModel(
                rawgGameApplication().container.genreRepository,
                rawgGameApplication().container.genreImageRepository,
                rawgGameApplication().container.rawgRepository,
                rawgGameApplication().container.cacheRepository
            )
        }

        // Initializer for GameListViewModel
        initializer {
            GameListViewModel(
                this.createSavedStateHandle(),
                rawgGameApplication().container.gameRepository,
                rawgGameApplication().container.rawgRepository,
                rawgGameApplication().container.gameImageRepository,
                rawgGameApplication().container.cacheRepository
            )
        }

        // Initializer for GameDetailsViewModel
        initializer {
            GameDetailsViewModel(
                this.createSavedStateHandle(),
                rawgGameApplication().container.gameRepository,
                rawgGameApplication().container.gameFavoriteRepository,
                rawgGameApplication().container.gameImageRepository,
                rawgGameApplication().container.rawgRepository,
                rawgGameApplication().container.cacheRepository
            )
        }

        // Initializer for GameSearchViewModel
        initializer {
            GameSearchViewModel(
                this.createSavedStateHandle(),
                rawgGameApplication().container.rawgRepository
            )
        }

        // Initializer for GameFavoriteViewModel
        initializer {
            GameFavoriteViewModel(
                rawgGameApplication().container.gameFavoriteRepository,
                rawgGameApplication().container.gameImageRepository
            )
        }

        // Initializer for AppSettingsViewModel
        initializer {
            AppSettingsViewModel(
                rawgGameApplication().container.paramManager,
                rawgGameApplication().container.cacheRepository
            )
        }

    }
}

fun CreationExtras.rawgGameApplication(): RawgGameApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RawgGameApplication)