package com.msukno.gameapprawg.data.genre_image

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.model.GenreImage
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreImageDao {

    @Query("SELECT * FROM genre_images")
    fun getAllImages(): Flow<List<GenreImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<GenreImage>)

    @Query("SELECT * FROM genre_images WHERE genreId IN (:ids)")
    suspend fun getImagesWithIds(ids: List<Int>): List<GenreImage>
}