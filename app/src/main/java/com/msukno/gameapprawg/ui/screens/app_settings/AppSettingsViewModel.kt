package com.msukno.gameapprawg.ui.screens.app_settings

import android.util.Log
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
 * ViewModel responsible for managing of the application parameters, navigation routes for different
 * layouts and image cache.
 */

class AppSettingsViewModel(
    private val paramManager: AppParamManager,
    private val cacheRepository: ImageCacheRepository
) : ViewModel(){
    companion object{
        const val GENRE_ID_KEY = "genreID"
        const val GENRE_NAME_KEY = "genreName"
    }

    // Holds the current route for each layout. Used for maintaining the same route after
    // configuration change
    var currentRoute = CurrentRoute()
    var initRouteUiState: InitRouteUiState by mutableStateOf(InitRouteUiState.Loading) //Initial route
        private set

    //Resets the pager after cache clearing
    var cacheState: AppCacheUiState by mutableStateOf(AppCacheUiState.Default)

    init {
        loadInitRoute()
    }

    fun writeParams(newGenreId: String, newGenreName: String){
        viewModelScope.launch {
            // Write the new parameters to shared preferences
            paramManager.writeParams(mapOf(GENRE_ID_KEY to newGenreId, GENRE_NAME_KEY to newGenreName))
        }
    }

    //Load the initial route on app startup
    private fun loadInitRoute(){
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
            initRouteUiState = InitRouteUiState.Complete(startingRoute)
        }
    }
    fun updateRouteCompact(route: String){
        currentRoute = currentRoute.copy(currentRouteCompact = route)
    }
    fun updateRouteList(route: String){
        currentRoute = currentRoute.copy(currentRouteList = route)
    }
    fun updateRouteDetail(route: String){
        currentRoute = currentRoute.copy(currentRouteDetail = route)
    }

    fun clearCache(){
        viewModelScope.launch {
            cacheRepository.clearCache()
            cacheState = AppCacheUiState.Cleared
        }
    }
}

data class CurrentRoute(
    val currentRouteCompact: String? = null,
    val currentRouteList: String? = null,
    val currentRouteDetail: String? = null
)

sealed interface InitRouteUiState {
    object Loading : InitRouteUiState
    data class Complete(val initialRoute: String) : InitRouteUiState
}

sealed interface AppCacheUiState {
    object Default : AppCacheUiState
    object Cleared : AppCacheUiState
}