package com.msukno.gameapprawg.data.game

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.msukno.gameapprawg.model.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("""
        SELECT * from games WHERE genreId = :selectedGenreId AND refreshTime LIKE :refTime AND sortKeys LIKE '-rating'
        ORDER BY rating DESC, released DESC, nextPageIndex ASC
    """)
    fun gamesByRatingDescAndDateDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game>

    @Query("""
        SELECT * from games WHERE genreId = :selectedGenreId AND refreshTime LIKE :refTime AND sortKeys LIKE '-released'
        ORDER BY released DESC, rating DESC, nextPageIndex ASC
    """)
    fun gamesByDateDescAndRatingDesc(selectedGenreId: Int, refTime: String): PagingSource<Int, Game>


    @Query("SELECT * FROM games")
    fun getAllGamesStream(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGame(id: Int): Game?

    @Update
    suspend fun update(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<Game>)

    @Query("SELECT * FROM games WHERE id IN (:ids)")
    suspend fun findGamesByIds(ids: List<Int>): List<Game>

    @Query("DELETE FROM games")
    suspend fun clearData()

    @Query("SELECT COUNT(*) FROM games")
    suspend fun gamesSize(): Int
}