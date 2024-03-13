package com.msukno.gameapprawg.data.game

import androidx.paging.PagingSource
import com.msukno.gameapprawg.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides methods for inserting, updating, deleting and retrieving of [Game] from
 * local database which is used for pagination.
 */
interface GameRepository {
    fun gamesByRatingDescAndDateDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game>
    fun gamesByDateDescAndRatingDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game>
    fun getAllGamesStream(): Flow<List<Game>>
    suspend fun getGame(id: Int): Game?
    suspend fun updateGame(game: Game)
    suspend fun findGamesByIds(ids: List<Int>): List<Game>
    suspend fun getGamesSize(): Int
    suspend fun insert(games: List<Game>)
    suspend fun clearAll()
}

class LocalGameRepository(val gameDao: GameDao): GameRepository {

    override fun gamesByRatingDescAndDateDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game> =
        gameDao.gamesByRatingDescAndDateDesc(selectedGenreId, refTime)

    override fun gamesByDateDescAndRatingDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game> =
        gameDao.gamesByDateDescAndRatingDesc(selectedGenreId, refTime)

    override fun getAllGamesStream(): Flow<List<Game>> = gameDao.getAllGamesStream()

    override suspend fun getGame(id: Int): Game? = gameDao.getGame(id)

    override suspend fun updateGame(game: Game) = gameDao.update(game)

    override suspend fun findGamesByIds(ids: List<Int>): List<Game> = gameDao.findGamesByIds(ids)

    override suspend fun getGamesSize(): Int = gameDao.gamesSize()

    override suspend fun insert(games: List<Game>) = gameDao.insertAll(games)

    override suspend fun clearAll() = gameDao.clearData()
}