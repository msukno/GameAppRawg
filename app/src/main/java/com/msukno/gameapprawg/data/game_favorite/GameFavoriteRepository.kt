package com.msukno.gameapprawg.data.game_favorite


import com.msukno.gameapprawg.model.GameFavorite
import kotlinx.coroutines.flow.Flow

/**
 * Repository provides methods for inserting, deleting and retrieving of [GameFavorite] from
 * local database that is used for storing favorite games picked by the user.
 */
interface GameFavoriteRepository {
    fun getAllGamesStream(): Flow<List<GameFavorite>>
    suspend fun getGame(gameId: Int): GameFavorite?
    suspend fun findGamesByIds(gameIds: List<Int>): List<GameFavorite>
    suspend fun insertAllGames(games: List<GameFavorite>)
    suspend fun removeGame(gameId: Int)
}

class LocalGameFavoriteRepository(private val gameFavoriteDao: GameFavoriteDao): GameFavoriteRepository{

    override fun getAllGamesStream(): Flow<List<GameFavorite>> = gameFavoriteDao.getAllStream()

    override suspend fun getGame(gameId: Int): GameFavorite? = gameFavoriteDao.getGame(gameId)

    override suspend fun findGamesByIds(gameIds: List<Int>): List<GameFavorite> = gameFavoriteDao.getGamesByIds(gameIds)

    override suspend fun insertAllGames(games: List<GameFavorite>) = gameFavoriteDao.insertAll(games)

    override suspend fun removeGame(gameId: Int) = gameFavoriteDao.remove(gameId)
}