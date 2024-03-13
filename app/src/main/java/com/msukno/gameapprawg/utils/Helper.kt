package com.msukno.gameapprawg.utils

import com.msukno.gameapprawg.ui.screens.game_list.GameSortKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HelperFunctions{

    fun dateTimeByThirdOfDay(): String {
        val currentDateTime = LocalDateTime.now()
        val hour = currentDateTime.hour / 8
        val adjustedDateTime = currentDateTime.withHour(hour)
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd H")
        return adjustedDateTime.format(formatter)
    }

    fun dateTimeByDay(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd")
        return currentDateTime.format(formatter)
    }
}