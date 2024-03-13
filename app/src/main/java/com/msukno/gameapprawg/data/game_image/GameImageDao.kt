package com.msukno.gameapprawg.data.game_image

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.GenreImage
import kotlinx.coroutines.flow.Flow

@Dao
interface GameImageDao {

    @Query("SELECT * FROM game_images")
    fun getAllImages(): Flow<List<GameImages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<GameImages>)

    @Query("SELECT * FROM game_images WHERE gameId IN (:ids)")
    suspend fun getImagesWithIds(ids: List<Int>): List<GameImages>

    @Query("SELECT * FROM game_images")
    suspend fun getAll(): List<GameImages>

    @Query("DELETE FROM game_images")
    suspend fun clearAll()
}