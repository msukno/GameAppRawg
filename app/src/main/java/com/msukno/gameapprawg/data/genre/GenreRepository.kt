package com.msukno.gameapprawg.data.genre

import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.Genre
import kotlinx.coroutines.flow.Flow

/**
 * Repository provides methods for inserting, deleting and retrieving of [Genre] from
 * local database.
 */
interface GenreRepository {
    fun getGenreByGamesCount(): Flow<List<Genre>>
    suspend fun getAll(): List<Genre>
    suspend fun insert(genres: List<Genre>)
    suspend fun clearAll()
}

class LocalGenreRepository(val genreDao: GenreDao): GenreRepository {
    override fun getGenreByGamesCount(): Flow<List<Genre>> = genreDao.genresByGamesCount()

    override suspend fun getAll(): List<Genre> = genreDao.getAll()

    override suspend fun insert(genres: List<Genre>) = genreDao.insertAll(genres)

    override suspend fun clearAll() = genreDao.clearData()
}