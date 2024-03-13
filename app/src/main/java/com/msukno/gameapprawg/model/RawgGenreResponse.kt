package com.msukno.gameapprawg.model

import kotlinx.serialization.Serializable

@Serializable
data class GenreId(
    val id: Int,
)

@Serializable
data class GenreResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<GenreId>
)

@Serializable
data class GenreDetailsResponse(
    val id: Int,
    val name: String,
    val games_count: Int,
    val image_background: String?,
    val description: String
)

fun GenreDetailsResponse.toGenre(): Genre = Genre(
    id = id,
    name = name,
    gamesCount = games_count,
    imageBackground = image_background.toString(),
    description = description
)


