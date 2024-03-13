package com.msukno.gameapprawg.model

import com.msukno.gameapprawg.ui.screens.game_list.GameSortKey
import kotlinx.serialization.Serializable

@Serializable
data class GameResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<GameResult>
)

@Serializable
data class GameResult(
    val id: Int,
    val name: String,
    val released: String?,
    val background_image: String?,
    val rating: Float,
    val ratings_count: Int,
    val platforms: List<PlatformWrapper>?
)

@Serializable
data class PlatformWrapper(
    val platform: Platform
)

@Serializable
data class Platform(
    val id: Int,
    val name: String
)

@Serializable
data class GameDetailsResponse(
    val id: Int,
    val name: String,
    val released: String?,
    val background_image: String?,
    val rating: Float,
    val ratings_count: Int,
    val platforms: List<PlatformWrapper>?,
    val description: String,
    val screenshots_count: Int
)

@Serializable
data class GameScreenShotResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<GameScreenShot>
)

@Serializable
data class GameScreenShot(
    val id: Int,
    val image: String,
    val hidden: Boolean = false,
    val width: Int,
    val height: Int
)

fun GameResult.toGame(
    genreId: Int,
    prevIndex: Int?,
    nextIndex: Int?,
    currentTime: String,
    sortKey: GameSortKey
): Game = Game(
    id = id,
    name = name,
    genreId = genreId,
    released = released.toString(),
    backgroundImage = background_image.toString(),
    rating = rating,
    ratingsCount = ratings_count,
    description = "",
    screenshots = setOf(),
    platforms = platforms?.map { wrapper -> wrapper.platform.name }?.toSet() ?: setOf(),
    refreshTime = currentTime,
    sortKeys = setOf(sortKey.value),
    prevPageIndex = prevIndex,
    nextPageIndex = nextIndex,
)