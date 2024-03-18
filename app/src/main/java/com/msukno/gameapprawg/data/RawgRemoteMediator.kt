package com.msukno.gameapprawg.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.network.RawgDataLoader
import com.msukno.gameapprawg.ui.common.GameLocation
import com.msukno.gameapprawg.ui.common.GameSortKey
import com.msukno.gameapprawg.ui.common.ImageType
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator implementation used for loading the next page from RAWG repository into the local database.
 */

@OptIn(ExperimentalPagingApi::class)
class RawgRemoteMediator(
    private val gameRepository: GameRepository,
    private val rawgDataLoader: RawgDataLoader,
    private val cacheRepository: ImageCacheRepository,
    private val selectedGenreId: Int,
    private val startKey: String, //we start paging from this position
    private val sortKey: GameSortKey,
): RemoteMediator<Int, Game>() {

    // Function to load data. This function is called when the PagingSource is running out of data.
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Game>): MediatorResult {
        // Determine the page key based on the load type
        val pageKey = when (loadType) {
            LoadType.REFRESH -> {
                // If refreshing, use the next page index of the item closest to the anchor position
                state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.nextPageIndex
                } ?: startKey.toInt() // If there's no anchor position, use the start key
            }
            LoadType.PREPEND -> {
                // If prepending, use the previous page index of the first item in the current list
                val firstItem = state.firstItemOrNull()
                firstItem?.prevPageIndex ?:
                return MediatorResult.Success(endOfPaginationReached = firstItem != null)
            }
            LoadType.APPEND -> {
                // If appending, use the next page index of the last item in the list
                val lastItem = state.lastItemOrNull()
                lastItem?.nextPageIndex ?:
                return MediatorResult.Success(endOfPaginationReached = lastItem != null)
            }
        }

        // Try to load the data
        return try {
            // Fetch one page of games from the RAWG API
            val games = rawgDataLoader.fetchOneGamePage(selectedGenreId, pageKey, sortKey)
            // Update the sort keys for games so they can be found by different sort methods
            // Without this, the sort keys for the same game would be overriden
            val currentGamesMap = gameRepository.findGamesByIds(games.map { it.id }).associateBy { it.id }
            val updatedGames = games.map { game ->
                currentGamesMap[game.id]?.let { game.copy(
                    sortKeys = it.sortKeys + game.sortKeys
                ) } ?: game
            }
            // Insert the updated games into the local database
            gameRepository.insert(updatedGames)
            // Update the image cache
            cacheRepository.updateGameCache(updatedGames.map { it.id }, ImageType.Background, GameLocation.GamesTable)
            MediatorResult.Success(endOfPaginationReached = false)
        }catch (e: HttpException) {
            Log.d("RemoteMediator", "HttpException: ${e.message}")
            MediatorResult.Error(e)
        }catch (e: IOException) {
            Log.d("RemoteMediator", "IOException: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}