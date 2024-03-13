package com.msukno.gameapprawg.data

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.msukno.gameapprawg.data.cache.ImageCacheManager
import com.msukno.gameapprawg.data.database.RawgLocalDatabase
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game.LocalGameRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.data.game_image.LocalGameImageRepository
import com.msukno.gameapprawg.data.genre.GenreRepository
import com.msukno.gameapprawg.data.genre.LocalGenreRepository
import com.msukno.gameapprawg.data.cache.ImageCacheRepository
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteRepository
import com.msukno.gameapprawg.data.game_favorite.LocalGameFavoriteRepository
import com.msukno.gameapprawg.data.genre_image.GenreImageRepository
import com.msukno.gameapprawg.data.genre_image.LocalGenreImageRepository
import com.msukno.gameapprawg.network.NetworkRawgRepository
import com.msukno.gameapprawg.network.RawgApiService
import com.msukno.gameapprawg.network.RawgRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * App container for Dependency injection.
 */

interface AppContainer {
    val rawgRepository: RawgRepository
    val genreRepository: GenreRepository
    val gameRepository: GameRepository
    val gameFavoriteRepository: GameFavoriteRepository
    val gameImageRepository: GameImageRepository
    val genreImageRepository: GenreImageRepository
    val imageDownloader: ImageDownloader
    val cacheRepository: ImageCacheRepository
    val paramManager: AppParamManager
}

class DefaultAppContainer(private val context: Context): AppContainer {
    private val BASE_URL = "https://api.rawg.io/api/"
    private val API_KEY = "dd794e5567ba474b91c9032df6604276"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()

            val requestBuilder = original.newBuilder()
                .url(url)

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    private val retrofitService: RawgApiService by lazy {
        retrofit.create(RawgApiService::class.java)
    }

    override val rawgRepository: RawgRepository by lazy {
        NetworkRawgRepository(retrofitService)
    }
    override val genreRepository: GenreRepository by lazy {
        LocalGenreRepository(RawgLocalDatabase.getDatabase(context).genreDao())
    }
    override val gameRepository: GameRepository by lazy {
        LocalGameRepository(RawgLocalDatabase.getDatabase(context).gameDao())
    }
    override val gameFavoriteRepository: GameFavoriteRepository by lazy {
        LocalGameFavoriteRepository(RawgLocalDatabase.getDatabase(context).gameFavoriteDao())
    }


    override val gameImageRepository: GameImageRepository by lazy{
        LocalGameImageRepository(RawgLocalDatabase.getDatabase(context).gameImageDao())
    }

    override val genreImageRepository: GenreImageRepository by lazy{
        LocalGenreImageRepository(RawgLocalDatabase.getDatabase(context).genreImageDao())
    }

    override val imageDownloader: ImageDownloader by lazy {
        CoilImageDownloader(context)
    }

    override val paramManager: AppParamManager by lazy {
        SharedPrefAppParamManager(context)
    }

    override val cacheRepository: ImageCacheRepository by lazy {
        ImageCacheManager(context, paramManager)
    }
}