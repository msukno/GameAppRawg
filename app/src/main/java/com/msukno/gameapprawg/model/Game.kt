package com.msukno.gameapprawg.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.msukno.gameapprawg.data.database.Converters

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val genreId: Int,
    val released: String?,
    val backgroundImage: String,
    val rating: Float,
    val ratingsCount: Int,
    val description: String,
    @TypeConverters(Converters::class)
    val screenshots: Set<String>,
    @TypeConverters(Converters::class)
    val platforms: Set<String>,
    @TypeConverters(Converters::class)
    val sortKeys: Set<String>,
    val refreshTime: String,
    val prevPageIndex: Int?,
    val nextPageIndex: Int?
)