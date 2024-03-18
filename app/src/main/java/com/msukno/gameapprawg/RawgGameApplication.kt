package com.msukno.gameapprawg

import android.app.Application
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import com.msukno.gameapprawg.data.AppContainer
import com.msukno.gameapprawg.data.DefaultAppContainer
import kotlinx.serialization.json.Json.Default.configuration

class RawgGameApplication: Application(), Configuration.Provider {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val factory = DelegatingWorkerFactory()
        factory.addFactory(GameWorkerFactory(
            container.gameRepository,
            container.gameFavoriteRepository,
            container.gameImageRepository
        ))
        factory.addFactory(GenreWorkerFactory(
            container.genreRepository,
            container.genreImageRepository,
            container.imageDownloader
        ))
        factory.addFactory(ClearWorkerFactory(
            container.gameRepository,
            container.gameImageRepository
        ))
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(factory)
            .build()
    }
}