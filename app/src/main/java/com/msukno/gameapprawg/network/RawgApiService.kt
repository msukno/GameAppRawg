package com.msukno.gameapprawg.network

import com.msukno.gameapprawg.model.GameDetailsResponse
import com.msukno.gameapprawg.model.GameResponse
import com.msukno.gameapprawg.model.GameScreenShotResponse
import com.msukno.gameapprawg.model.GenreDetailsResponse
import com.msukno.gameapprawg.model.GenreResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {
    @GET("genres")
    suspend fun getGenres(@Query("page") page: String): GenreResponse

    @GET("games")
    suspend fun getGames(
        @Query("genres") genres: String,
        @Query("page") page: String,
        @Query("page_size") pageSize: Int,
        @Query("ordering") ordering: String
    ): GameResponse

    @GET("genres/{id}")
    suspend fun getGenreDetails(@Path("id") id: Int): GenreDetailsResponse

    @GET("games/{id}")
    suspend fun getGameDetails(@Path("id") id: Int): GameDetailsResponse

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenShots(@Path("id") id: Int): GameScreenShotResponse

    @GET("games")
    suspend fun searchGames(
        @Query("genres") genres: String,
        @Query("page") page: String,
        @Query("page_size") pageSize: Int,
        @Query("ordering") ordering: String,
        @Query("search_precise") searchPrecise: Boolean,
        @Query("search_exact") searchExact: Boolean,
        @Query("search") search: String,
    ): GameResponse
}