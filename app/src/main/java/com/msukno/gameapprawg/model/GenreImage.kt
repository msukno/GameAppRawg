package com.msukno.gameapprawg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genre_images")
data class GenreImage(
    @PrimaryKey(autoGenerate = false)
    val genreId: Int,
    val imagePath: String,
    val refreshTime: String
)