package com.msukno.gameapprawg.data.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msukno.gameapprawg.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<Genre>)

    @Query("SELECT * from genres ORDER BY gamesCount ASC")
    fun genresByGamesCount(): Flow<List<Genre>>

    @Query("SELECT * from genres")
    suspend fun getAll(): List<Genre>

    @Query("DELETE FROM genres")
    suspend fun clearData()
}