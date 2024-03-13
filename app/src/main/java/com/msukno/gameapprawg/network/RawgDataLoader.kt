package com.msukno.gameapprawg.network

import android.util.Log
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.model.Genre
import com.msukno.gameapprawg.model.toGame
import com.msukno.gameapprawg.model.toGenre
import com.msukno.gameapprawg.ui.screens.game_details.GameDetails
import com.msukno.gameapprawg.ui.screens.game_list.GameSortKey
import com.msukno.gameapprawg.utils.HelperFunctions
import kotlinx.coroutines.delay
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.HttpException
import java.io.IOException


private const val PAGE_SIZE = 40
private const val START_PAGE = 1
private const val SCREENSHOTS_LIMIT = 3
private const val RETRY_LIMIT = 3
private const val RETRY_DELAY_MILLIS = 1500L
private const val TAG = "RawgDataLoader"

/**
 * Data loader class which uses RawgRepository for fetching game and genre data from RAWG database.
 */
class RawgDataLoader(private val rawgRepository: RawgRepository){

    suspend fun fetchAllGenres(startPage: String): List<Genre> {
        var nextPage: String? = startPage
        val genres = mutableListOf<Genre>()
        safeApiCallWithRetry {
            while (nextPage != null) {
                val genreResponse = rawgRepository.getGenres(nextPage!!)
                for (genreId in genreResponse.results) {
                    val genre = rawgRepository.getGenreDetails(genreId.id).toGenre()
                    genres.add(genre)
                }
                nextPage = genreResponse.next
            }
        }
        return genres.toList()
    }


    suspend fun fetchOneGamePage(genreId: Int, pageKey: Int, sortKey: GameSortKey): List<Game> {
        val gameResponse = rawgRepository.getGames(
            genreId = genreId,
            page = pageKey.toString(),
            pageSize = PAGE_SIZE,
            ordering = sortKey.value
        )
        return gameResponse.results.map {
            it.toGame(
                genreId = genreId,
                prevIndex = extractPageKey(gameResponse.previous),
                nextIndex = extractPageKey(gameResponse.next),
                currentTime = HelperFunctions.dateTimeByThirdOfDay(),
                sortKey = sortKey
            )
        }
    }

    suspend fun fetchGameDetails(id: Int): GameDetails?{
        return  safeApiCallWithRetry {
            val screenShots: MutableSet<String> = mutableSetOf()
            val gameDetailsResponse = rawgRepository.getGameDetails(id)
            if(gameDetailsResponse.screenshots_count > 0){
                val screenshotsResponse = rawgRepository.getGameScreenShots(id)
                for(i in 0..<gameDetailsResponse.screenshots_count) {
                    if (i > SCREENSHOTS_LIMIT - 1 ) break
                    screenShots.add(screenshotsResponse.results[i].image)
                }
            }
            GameDetails(
                id = gameDetailsResponse.id,
                name = gameDetailsResponse.name,
                released = gameDetailsResponse.released ?: "",
                backgroundImage = gameDetailsResponse.background_image ?: "",
                rating = gameDetailsResponse.rating,
                ratingsCount = gameDetailsResponse.ratings_count,
                description = gameDetailsResponse.description,
                screenshots = screenShots,
                platforms = gameDetailsResponse.platforms?.map { wrapper -> wrapper.platform.name }?.toSet() ?: setOf()
            )
        }
    }

    // By default search is limited to fetch the first 100 games from first 5 pages that satisfy the search query
    // Games are ordered by rating. Search method is quite simple and it filters the games by exact occurrence of search query
    // in game name.
    suspend fun performSearch(
        genreId: Int,
        query: String,
        gameThreshold: Int = 100,
        pageLimit: Int = 5
    ): List<Game>{
        val result: MutableList<Game> = mutableListOf()
        var nextPage : Int? = START_PAGE
        var pagesSearched = 0

        while(nextPage != null){

            val gameResponse = safeApiCallWithRetry {
                rawgRepository.searchGames(genreId, nextPage.toString(), PAGE_SIZE,
                    ordering = "-rating", searchPrecise = true, searchExact = true, search = query
                )
            }
            //If there is no internet access or if error occurs gameResponse==null
            gameResponse?.let { response ->
                pagesSearched++
                val validGames = response.results.filter {
                    it.name.lowercase().contains(query.lowercase())
                }.map {
                    it.toGame(
                        genreId = genreId,
                        prevIndex = null,
                        nextIndex = null,
                        currentTime = HelperFunctions.dateTimeByThirdOfDay(),
                        sortKey = GameSortKey.ratingDESC
                    ) }
                result.addAll(validGames)
                if(result.size > gameThreshold || pagesSearched > pageLimit) return result.toList()
                nextPage = extractPageKey(response.next)
            } ?: return result.toList()
        }
        return result.toList()
    }

    private fun extractPageKey(url: String?): Int?{
        val httpUrl = url?.toHttpUrl()
        return httpUrl?.queryParameter("page")?.toInt()
    }

    // If error occurs the method will repeat the request with delay
    private suspend fun <T> safeApiCallWithRetry(call: suspend () -> T): T? {
        for(i in 1..RETRY_LIMIT){
            try {
                return call()
            }
            catch (e: HttpException) {
                Log.d("mytag", "HttpException while making API call. \n ${e.message}")
            }catch (e: IOException) {
                Log.d("mytag", "IOException while making API call. \n ${e.message}")
            }finally {
                Log.d(TAG, "Retrying network request: $i")
                delay(RETRY_DELAY_MILLIS)
            }
        }
        return null
    }
}