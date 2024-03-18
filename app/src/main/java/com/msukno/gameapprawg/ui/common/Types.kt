package com.msukno.gameapprawg.ui.common

// Sort types
enum class GameSortKey(var value: String) {
    RatingDESC(value = "-rating"),
    ReleasedDESC(value = "-released"),
}

// Cache image types
enum class ImageType{
    Screenshot,
    Background
}


// Game data location
enum class GameLocation{
    GamesTable, FavoritesTable
}