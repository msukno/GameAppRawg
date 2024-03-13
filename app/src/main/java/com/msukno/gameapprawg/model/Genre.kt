package com.msukno.gameapprawg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val gamesCount: Int,
    val imageBackground: String,
    val description: String
)