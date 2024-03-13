package com.msukno.gameapprawg.ui.screens.app_settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.AppParamManager
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the application parameters and cache.
 */

class AppSettingsViewModel(
    private val paramManager: AppParamManager,
    private val cacheRepository: ImageCacheRepository
) : ViewModel(){
    companion object{
        const val GENRE_ID_KEY = "genreID"
        const val GENRE_NAME_KEY = "genreName"
    }

    var paramsState: AppParamsUiState by mutableStateOf(AppParamsUiState.Loading)
    var cacheState: AppCacheUiState by mutableStateOf(AppCacheUiState.Default)

    init {
        viewModelScope.launch {
            val loadedParams = paramManager.loadParams(mapOf(GENRE_ID_KEY to "", GENRE_NAME_KEY to ""))
            paramsState = AppParamsUiState.Complete(loadedParams)
        }
    }

    /**
     * Updates the application parameters with the new genre ID and name.
     */
    fun updateParams(newGenreId: String, newGenreName: String){
        viewModelScope.launch {
            // Write the new parameters to shared preferences
            paramManager.writeParams(mapOf(GENRE_ID_KEY to newGenreId, GENRE_NAME_KEY to newGenreName))
        }
    }

    fun clearCache(){
        viewModelScope.launch {
            cacheRepository.clearCache()
            cacheState = AppCacheUiState.Cleared
        }
    }
}

sealed interface AppParamsUiState {
    object Loading : AppParamsUiState
    data class Complete(val params: Map<String, String>) : AppParamsUiState
}


sealed interface AppCacheUiState {
    object Default : AppCacheUiState
    object Cleared : AppCacheUiState
}