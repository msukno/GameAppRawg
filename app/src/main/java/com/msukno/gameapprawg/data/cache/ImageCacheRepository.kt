package com.msukno.gameapprawg.data.cache

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.msukno.gameapprawg.data.AppParamManager
import com.msukno.gameapprawg.ui.screens.common.ImageType
import com.msukno.gameapprawg.ui.screens.game_details.GameLocation


/**
 * Repository provides methods for storing and clearing game and genre images from local cache
 */
interface ImageCacheRepository {
    fun updateGameCache(gameIDs: List<Int>, imageType: ImageType, gameLocation: GameLocation)
    fun updateGenreCache()
    fun clearCache()
}

private const val GAME_WORK_NAME = "GameCacheWork"
private const val GENRE_WORK_NAME = "GenreCacheWork"
private const val CLEAR_WORK_NAME = "ClearCacheWork"
const val IMG_CACHE_DIR = "img_cache"

class ImageCacheManager(val context: Context, val paramManager: AppParamManager) : ImageCacheRepository {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Creates the new unique work for updating the game cache
     */
    override fun updateGameCache(gameIDs: List<Int>, imageType: ImageType, gameLocation: GameLocation) {

        val workRequest = OneTimeWorkRequest.Builder(GameCacheWorker::class.java)
            .setInputData(createInputDataForCacheRequest(gameIDs, imageType, gameLocation))
            .build()

        //Append the work if the one already exists
        val uniqueWork = workManager.beginUniqueWork(
            GAME_WORK_NAME, ExistingWorkPolicy.APPEND, workRequest
        )

        uniqueWork.enqueue()
    }

    private fun createInputDataForCacheRequest(
        ids: List<Int>,
        imageType: ImageType,
        gameLocation: GameLocation
    ): Data {
        val builder = Data.Builder()
        builder.putIntArray("IDs", ids.toIntArray())
            .putString("imageType", imageType.name)
            .putString("gameLocation", gameLocation.name)
        return builder.build()
    }

    /**
     * Creates the new unique work for updating the genre cache
     */
    override fun updateGenreCache() {
        val workRequest = OneTimeWorkRequest.Builder(GenreCacheWorker::class.java)
            .build()

        //Replace the work if the one already exists
        val uniqueWork = workManager.beginUniqueWork(
            GENRE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest
        )
        uniqueWork.enqueue()
    }

    /**
     * Creates the new unique work for clearing the game cache
     */
    override fun clearCache() {
        val workRequest = OneTimeWorkRequest.Builder(ClearCacheWorker::class.java)
            .build()

        //Keep the existing work
        val uniqueWork = workManager.beginUniqueWork(
            CLEAR_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest
        )
        uniqueWork.enqueue()
    }
}