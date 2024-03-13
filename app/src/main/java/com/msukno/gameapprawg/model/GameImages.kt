package com.msukno.gameapprawg.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.msukno.gameapprawg.data.database.Converters


@Entity(tableName = "game_images")
data class GameImages(
    @PrimaryKey(autoGenerate = false)
    val gameId: Int,
    val background: String? = null,
    @TypeConverters(Converters::class)
    val screenshots: Set<String>? = null,
    val backRefreshTime: String = "",
    val screenRefreshTime: String = ""
)