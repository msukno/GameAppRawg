package com.msukno.gameapprawg.ui.screens.game_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.model.GameFavorite
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.toGameDetails
import com.msukno.gameapprawg.network.RawgDataLoader
import com.msukno.gameapprawg.network.RawgRepository
import com.msukno.gameapprawg.ui.screens.common.ImageType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Enum class for the location of the game data.
 */
enum class GameLocation{
    games_table, favorites_table, unknown
}

/**
 * ViewModel for the GameDetails screen. It fetches game details from the local database and the RAWG,
 * and provides them to the UI for user viewing. The ViewModel also manages the UI state of the game details
 * and a cache of game images.
 */
class GameDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val gameFavoriteRepository: GameFavoriteRepository,
    private val gameImageRepository: GameImageRepository,
    private val rawgRepository: RawgRepository,
    private val cacheRepository: ImageCacheRepository
) : ViewModel() {

    // In-memory cache of game image paths
    var imagePathsCache: Map<Int, GameImages> = mapOf()


    var uiState: GameDetailsUiState by mutableStateOf(GameDetailsUiState.Loading)
        private set

    // Fetch the ID of the game selected by the user
    private val gameId: Int = checkNotNull(savedStateHandle[GameDetailsDestination.gameIdArg])

    // Tells whether the game is in the favorites or not
    private var favorite = false

    init {
        loadGameDetails()
    }

    /**
     * Load details of the game whose ID equals to above obtained gameId. First try loading game
     * details from the local cache. Also try loading fresh game details from the web. Depending on the result,
     * refresh the data or keep the old data (if any).
     */
    private fun loadGameDetails(){
        viewModelScope.launch {
            // Fetch all game image paths and load them in memory
            gameImageRepository.getAllImagesStream().first().let{ imagesList ->
                imagePathsCache = imagesList.associateBy { it.gameId }
            }

            var gameInGames: Game? = null
            //Try fetching the game from favorites
            val gameInFavorite: GameFavorite? = gameFavoriteRepository.getGame(gameId)
            val dataLoader = RawgDataLoader(rawgRepository)

            // Check if the game is in the favorites list
            if (gameInFavorite != null){
                favorite = true
                updateUiState(gameInFavorite.toGameDetails(), favorite)
            } else {
                // Check if the game is in the games table
                gameInGames = gameRepository.getGame(gameId)
                gameInGames?.let {
                    updateUiState(gameInGames.toGameDetails(), favorite)
                }
            }
            // Get fresh game details from the RAWG and update the state if success
            val gameDetails: GameDetails? = dataLoader.fetchGameDetails(gameId)
            gameDetails?.let { updateUiState(it, favorite) }
            // Update the appropriate cache table if fresh data is fetched
            if (gameInFavorite != null) {
                gameDetails?.let { freshDetails ->
                    gameFavoriteRepository.insertAllGames(listOf(freshDetails.toGameFavorite()))
                    cacheRepository.updateGameCache(listOf(gameId), ImageType.Screenshot,
                        GameLocation.favorites_table
                    )
                }
            }else{
                if (gameInGames != null){
                    gameDetails?.let { freshDetails ->
                        gameRepository.insert(listOf(gameInGames.copy(
                            description = freshDetails.description,
                            screenshots = freshDetails.screenshots
                        )))
                        cacheRepository.updateGameCache(listOf(gameId), ImageType.Screenshot,
                            GameLocation.games_table
                        )
                    }
                }
            }
        }
    }

    /**
     * Updates the UI state with the new game details.
     */
    fun updateUiState(gameDetails: GameDetails, favorite: Boolean){
        uiState = GameDetailsUiState.Complete(gameDetails, favorite)
    }

    /**
     * Updates the favorites.
     * If the game is already in the favorites list, remove it from the list.
     * If the game is not in the favorites list, add it to the list and update the cache.
     */
    fun updateFavorites(){
        viewModelScope.launch {
            val stateComplete = uiState as GameDetailsUiState.Complete
            val currentFavoriteState = stateComplete.favorite
            updateUiState(stateComplete.gameDetails, !currentFavoriteState)
            if(currentFavoriteState) gameFavoriteRepository.removeGame(gameId)
            else {
                gameFavoriteRepository.insertAllGames(listOf(stateComplete.gameDetails.toGameFavorite()))
                cacheRepository.updateGameCache(listOf(gameId), ImageType.Screenshot,
                    GameLocation.favorites_table
                )
            }
        }
    }
}

/**
 * UI state for the game details.
 */
sealed interface GameDetailsUiState {
    object Loading : GameDetailsUiState
    data class Complete(
        val gameDetails: GameDetails = GameDetails(),
        val favorite: Boolean = false
    ) : GameDetailsUiState
}
/**
 * Data class for the game details.
 */
data class GameDetails(
    val id: Int = 0,
    val name: String = "",
    val released: String = "",
    val backgroundImage: String = "",
    val rating: Float = 0.toFloat(),
    val ratingsCount: Int = 0,
    val description: String = "",
    val screenshots: Set<String> = setOf(),
    val platforms: Set<String> = setOf(),
)

/**
 * Extension function to convert a Game to GameDetails object.
 */
fun Game.toGameDetails() : GameDetails = GameDetails(
    id = id,
    name = name,
    released = released ?: "",
    backgroundImage = backgroundImage,
    rating = rating,
    ratingsCount = ratingsCount,
    description = description,
    screenshots = screenshots,
    platforms = platforms
)

/**
 * Extension function to convert a GameDetails to a GameFavorite object.
 */
fun GameDetails.toGameFavorite(): GameFavorite = GameFavorite(
    id = id,
    name = name,
    released = released,
    backgroundImage = backgroundImage,
    rating = rating,
    ratingsCount = ratingsCount,
    description = description,
    screenshots = screenshots,
    platforms = platforms
)
