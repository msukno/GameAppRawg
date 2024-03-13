package com.msukno.gameapprawg.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msukno.gameapprawg.data.game.GameDao
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteDao
import com.msukno.gameapprawg.data.game_image.GameImageDao
import com.msukno.gameapprawg.data.genre.GenreDao
import com.msukno.gameapprawg.data.genre_image.GenreImageDao
import com.msukno.gameapprawg.model.Game
import com.msukno.gameapprawg.model.GameFavorite
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.Genre
import com.msukno.gameapprawg.model.GenreImage

@TypeConverters(Converters::class)
@Database(
    entities = [Genre::class, Game::class, GameFavorite::class, GameImages::class, GenreImage::class],
    version = 32,
    exportSchema = false
)
abstract class RawgLocalDatabase : RoomDatabase() {

    abstract fun genreDao(): GenreDao
    abstract fun gameDao(): GameDao
    abstract fun gameFavoriteDao(): GameFavoriteDao
    abstract fun gameImageDao(): GameImageDao
    abstract fun genreImageDao(): GenreImageDao

    companion object {
        @Volatile
        private var Instance: RawgLocalDatabase? = null

        fun getDatabase(context: Context): RawgLocalDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, RawgLocalDatabase::class.java, "rawg_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}