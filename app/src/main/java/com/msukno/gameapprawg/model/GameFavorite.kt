package com.msukno.gameapprawg.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.msukno.gameapprawg.data.database.Converters
import com.msukno.gameapprawg.ui.screens.game_details.GameDetails

@Entity(tableName = "game_favorite")
data class GameFavorite(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val released: String,
    val backgroundImage: String,
    val rating: Float,
    val ratingsCount: Int,
    val description: String,
    @TypeConverters(Converters::class)
    val screenshots: Set<String>,
    @TypeConverters(Converters::class)
    val platforms: Set<String>
)

fun GameFavorite.toGame() = Game(
    id = id,
    name = name,
    released = released,
    backgroundImage = backgroundImage,
    rating = rating,
    ratingsCount = ratingsCount,
    description = description,
    screenshots = screenshots,
    platforms = platforms,
    genreId = 0,
    sortKeys = setOf(),
    refreshTime = "",
    prevPageIndex = null,
    nextPageIndex = null
)

fun GameFavorite.toGameDetails() = GameDetails(
    id = id,
    name = name,
    released = released,
    backgroundImage = backgroundImage,
    rating = rating,
    ratingsCount = ratingsCount,
    description = description,
    screenshots = screenshots,
    platforms = platforms
)
