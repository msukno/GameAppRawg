package com.msukno.gameapprawg.ui.screens.common

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