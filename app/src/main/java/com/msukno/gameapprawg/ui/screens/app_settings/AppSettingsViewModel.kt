package com.msukno.gameapprawg.ui.screens.app_settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.AppParamManager
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.ui.screens.game_favorite.GameFavoriteDestination
import com.msukno.gameapprawg.ui.screens.game_list.GameListDestination
import com.msukno.gameapprawg.ui.screens.genre_selection.GenreSelectionDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private val _navGraphUiState = MutableStateFlow<NavGraphUiState>(NavGraphUiState.Loading)
    val navGraphUiState: StateFlow<NavGraphUiState> = _navGraphUiState
    var cacheState: AppCacheUiState by mutableStateOf(AppCacheUiState.Default)

    init {
        loadInitRoute()
    }

    fun updateNavGraphUiState(details: NavGraphDetails){
        _navGraphUiState.update { NavGraphUiState.Complete(details) }
    }
    fun writeParams(newGenreId: String, newGenreName: String){
        viewModelScope.launch {
            // Write the new parameters to shared preferences
            paramManager.writeParams(mapOf(GENRE_ID_KEY to newGenreId, GENRE_NAME_KEY to newGenreName))
        }
    }


    fun loadInitRoute(){
        viewModelScope.launch {
            val loadedParams = paramManager.loadParams(mapOf(GENRE_ID_KEY to "", GENRE_NAME_KEY to ""))
            val idKey: String = checkNotNull(loadedParams[GENRE_ID_KEY])
            val nameKey: String = checkNotNull(loadedParams[GENRE_NAME_KEY])
            val startingRoute = try {
                val genreId = idKey.toInt()
                "${GameListDestination.route}/$genreId/$nameKey"
            }catch (throwable: Throwable){
                GenreSelectionDestination.route
            }
            updateNavGraphUiState(NavGraphDetails(
                startRouteCompact = startingRoute,
                startRouteList = startingRoute,
                startRouteDetail = GameFavoriteDestination.route
            ))
        }
    }

    fun updateRouteCompact(newRoute: String){
        when(val state = navGraphUiState.value){
            is NavGraphUiState.Complete ->
                updateNavGraphUiState(state.navGraphDetails.copy(startRouteCompact = newRoute))
            else -> {}
        }
    }
    fun updateRouteList(newRoute: String){
        when(val state = navGraphUiState.value){
            is NavGraphUiState.Complete ->
                    updateNavGraphUiState(state.navGraphDetails.copy(startRouteList = newRoute))
            else -> {}
        }
    }
    fun updateRouteDetail(newRoute: String){
        when(val state = navGraphUiState.value){
            is NavGraphUiState.Complete ->
                    updateNavGraphUiState(state.navGraphDetails.copy(startRouteDetail = newRoute))
            else -> {}
        }
    }

    fun clearCache(){
        viewModelScope.launch {
            cacheRepository.clearCache()
            cacheState = AppCacheUiState.Cleared
        }
    }
}

data class NavGraphDetails(
    val startRouteCompact: String,
    val startRouteList: String,
    val startRouteDetail: String
)

sealed interface NavGraphUiState {
    object Loading : NavGraphUiState
    data class Complete(val navGraphDetails: NavGraphDetails) : NavGraphUiState
}

sealed interface AppCacheUiState {
    object Default : AppCacheUiState
    object Cleared : AppCacheUiState
}