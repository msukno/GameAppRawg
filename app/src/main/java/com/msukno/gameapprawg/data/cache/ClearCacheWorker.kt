package com.msukno.gameapprawg.data.cache

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "ClearCacheWorker"
class ClearCacheWorker(
    private val gameRepository: GameRepository,
    private val gameImageRepository: GameImageRepository,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        makeStatusNotification("Clearing cache.", applicationContext)
        return withContext(Dispatchers.IO){
            return@withContext try {
                val allImages = gameImageRepository.getAll()
                allImages.forEach { gameImgs ->
                    val paths: MutableList<String> = gameImgs.screenshots?.toMutableList() ?: mutableListOf()
                    gameImgs.background?.let { background -> paths.add(background)}
                    paths.forEach { path ->
                        try {
                            val file = File(path)
                            if (file.exists()) {
                                val deleted = file.delete()
                                if (deleted) {
                                        Log.d(TAG, "Deleted file at $path")
                                } else {
                                        Log.d(TAG, "Failed to delete file at $path")
                                }
                            } else {
                                    Log.d(TAG, "File at $path does not exist")
                            }
                        }catch (throwable: Throwable){
                                Log.d(TAG, "Error while deleting a file: $path")
                        }
                    }
                }
                gameImageRepository.clearCache()
                gameRepository.clearAll()
                makeStatusNotification("Done.", applicationContext)
                Result.success()
            }catch (throwable: Throwable){
                Log.e(TAG, "Error while clearing cache", throwable)
                Result.failure()
            }
        }
    }
}