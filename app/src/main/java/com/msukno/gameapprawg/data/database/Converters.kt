package com.msukno.gameapprawg.data.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromSet2Text(textSet: Set<String>): String{
        return textSet.joinToString(separator = ";")
    }

    @TypeConverter
    fun fromText2Set(text: String): Set<String>{
        val parts = text.split(";")
        return if (parts.isEmpty()) setOf()
        else parts.map { it.trim() }.toSet()
    }
}