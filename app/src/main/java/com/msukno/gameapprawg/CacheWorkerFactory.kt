package com.msukno.gameapprawg

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.msukno.gameapprawg.data.AppParamManager
import com.msukno.gameapprawg.data.ImageDownloader
import com.msukno.gameapprawg.data.cache.ClearCacheWorker
import com.msukno.gameapprawg.data.cache.GameCacheWorker
import com.msukno.gameapprawg.data.cache.GenreCacheWorker
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.data.genre.GenreRepository
import com.msukno.gameapprawg.data.genre_image.GenreImageRepository

/**
 * Factories for providing worker instances
 */

class GameWorkerFactory(
    private val gameRepository: GameRepository,
    private val gameFavoriteRepository: GameFavoriteRepository,
    private val gameImageRepository: GameImageRepository,
    private val paramManager: AppParamManager
    ) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when(workerClassName){
            GameCacheWorker::class.java.name -> {
                GameCacheWorker(
                    gameRepository,
                    gameFavoriteRepository,
                    gameImageRepository,
                    paramManager,
                    appContext,
                    workerParameters
                )
            }
            else -> null
        }
    }
}

class GenreWorkerFactory(
    private val genreRepository: GenreRepository,
    private val genreImageRepository: GenreImageRepository,
    private val imageDownloader: ImageDownloader
) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName){
            GenreCacheWorker::class.java.name -> {
                GenreCacheWorker(
                    genreRepository,
                    genreImageRepository,
                    imageDownloader,
                    appContext,
                    workerParameters
                )
            }
            else -> null
        }
    }
}

class ClearWorkerFactory(
    private val gameRepository: GameRepository,
    private val gameImageRepository: GameImageRepository
) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName){
            ClearCacheWorker::class.java.name -> {
                ClearCacheWorker(
                    gameRepository,
                    gameImageRepository,
                    appContext,
                    workerParameters
                )
            }
            else -> null
        }
    }
}