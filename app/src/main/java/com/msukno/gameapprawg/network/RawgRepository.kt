package com.msukno.gameapprawg.network

import android.util.Log
import com.msukno.gameapprawg.model.GameDetailsResponse
import com.msukno.gameapprawg.model.GameResponse
import com.msukno.gameapprawg.model.GameScreenShotResponse
import com.msukno.gameapprawg.model.Genre
import com.msukno.gameapprawg.model.GenreDetailsResponse
import com.msukno.gameapprawg.model.GenreResponse
import com.msukno.gameapprawg.model.toGenre

/**
 * Repository that provides methods for accessing data in RAWG database
 */
interface RawgRepository {
    suspend fun getGenres(page: String): GenreResponse

    suspend fun getGenreDetails(id: Int): GenreDetailsResponse

    suspend fun getGames(genreId: Int, page: String, pageSize: Int, ordering: String): GameResponse

    suspend fun getGameDetails(id: Int): GameDetailsResponse

    suspend fun getGameScreenShots(id: Int): GameScreenShotResponse

    suspend fun searchGames(
        genreId: Int,
        page: String,
        pageSize: Int,
        ordering: String,
        searchPrecise: Boolean,
        searchExact: Boolean,
        search: String): GameResponse
}

class NetworkRawgRepository(private val rawgApiService: RawgApiService): RawgRepository {

    override suspend fun getGenres(page: String): GenreResponse = rawgApiService.getGenres(page)

    override suspend fun getGenreDetails(id: Int): GenreDetailsResponse = rawgApiService.getGenreDetails(id)

    override suspend fun getGames(genreId: Int, page: String, pageSize: Int, ordering: String):
            GameResponse = rawgApiService.getGames(genreId.toString(), page, pageSize, ordering)

    override suspend fun getGameDetails(id: Int): GameDetailsResponse = rawgApiService.getGameDetails(id)

    override suspend fun getGameScreenShots(id: Int): GameScreenShotResponse = rawgApiService.getGameScreenShots(id)

    override suspend fun searchGames(
        genreId: Int,
        page: String,
        pageSize: Int,
        ordering: String,
        searchPrecise: Boolean,
        searchExact: Boolean,
        search: String
    ): GameResponse = rawgApiService.searchGames(
        genreId.toString(), page, pageSize, ordering, searchPrecise, searchExact, search
    )

}