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

    @Query("SELECT COUNT(*) FROM games WHERE sortKeys LIKE '-rating'")
    suspend fun countGamesRatingSortKey(): Int

    @Query("SELECT COUNT(*) FROM games WHERE sortKeys LIKE 'name'")
    suspend fun countGamesNameSortKey(): Int

    @Query("""
        SELECT COUNT(*) FROM games 
        WHERE rating > (SELECT rating FROM games WHERE id = :gameId)
    """)
    suspend fun countGamesWithHigherRating(gameId: Int): Int

    @Query("""
        SELECT COUNT(*) FROM games
        WHERE rating = (SELECT rating FROM games WHERE id = :gameId) AND id < :gameId
    """)
    suspend fun countGamesWithSameRatingAndLowerId(gameId: Int): Int

    @Query("SELECT * FROM games ORDER BY rating DESC, id ASC LIMIT :size OFFSET :offset")
    suspend fun getBatch( offset: Int, size: Int): List<Game>

}