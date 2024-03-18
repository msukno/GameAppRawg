package com.msukno.gameapprawg.ui.screens.game_list
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.msukno.gameapprawg.data.RawgRemoteMediator
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.network.RawgDataLoader
import com.msukno.gameapprawg.network.RawgRepository
import com.msukno.gameapprawg.ui.common.GameSortKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalArgumentException

@OptIn(ExperimentalPagingApi::class)
/**
 * UI state for the GameList screen. It uses a RemoteMediator for fetching data from the RAWG,
 * and a PagingSource object returned by GameRepository for paging from the local database.
 */
class GameListUiState(
    rawgRepository: RawgRepository,
    private val gameRepository: GameRepository,
    cacheRepository: ImageCacheRepository,
    scope: CoroutineScope,
    val params: GameListParams
){
    // Flow of paginated game data
    val gameList: Flow<PagingData<Game>> = Pager(
        config = PagingConfig(pageSize = params.pageSize, enablePlaceholders = true),
        remoteMediator = RawgRemoteMediator(
            gameRepository,
            RawgDataLoader(rawgRepository),
            cacheRepository,
            selectedGenreId = params.genreId,
            startKey = params.startKey,
            sortKey = params.sortKeys.first()
        ),
        pagingSourceFactory = { getPagingSource(params.sortKeys, params.validityPeriod) }
    )
        .flow
        .cachedIn(scope)

    /**
     * Returns a PagingSource for fetching games from the local database based on the sort keys.
     */
    private fun getPagingSource(sortKeys: List<GameSortKey>, validityPeriod: String): PagingSource<Int, Game> {
        val sortKey = sortKeys.map { it.value }.joinToString(separator = ";")
        return when(sortKey){
            "-rating" -> gameRepository.gamesByRatingDescAndDateDesc(params.genreId, validityPeriod) // Rating DESC
            "-released" -> gameRepository.gamesByDateDescAndRatingDesc(params.genreId, validityPeriod) // Date DESC
            "-rating;-released" -> gameRepository.gamesByRatingDescAndDateDesc(params.genreId, validityPeriod) // Rating DESC, Date DESC
            "-released;-rating" -> gameRepository.gamesByDateDescAndRatingDesc(params.genreId, validityPeriod) // Date DESC, Rating DESC
            else -> throw IllegalArgumentException("Invalid sort key: $sortKey")
        }
    }
}

/**
 * Parameters for fetching and sorting games.
 */
data class GameListParams(
    val startKey: String,
    val pageSize: Int,
    val genreId: Int, // Games from all genres are stores in the same table (Games). This param is used to filter them.
    val validityPeriod: String, // This param is used to refresh the cache by showing only the games that match this period.
                                // If there are no games that match the set period the new page is requested from RemoteMediator
    val sortKeys: List<GameSortKey>
)
