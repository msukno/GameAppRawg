package com.msukno.gameapprawg.data.game_favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msukno.gameapprawg.model.GameFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface GameFavoriteDao {

    @Query("SELECT * FROM game_favorite")
    fun getAllStream(): Flow<List<GameFavorite>>

    @Query("SELECT * FROM game_favorite WHERE id = :gameId")
    suspend fun getGame(gameId: Int): GameFavorite?

    @Query("SELECT * FROM game_favorite WHERE id IN (:gameIds)")
    suspend fun getGamesByIds(gameIds: List<Int>): List<GameFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<GameFavorite>)

    @Query("DELETE FROM game_favorite WHERE id = :gameId")
    suspend fun remove(gameId: Int)
}