package com.msukno.gameapprawg.data.cache

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msukno.gameapprawg.data.ImageDownloader
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.data.genre.GenreRepository
import com.msukno.gameapprawg.data.genre_image.GenreImageRepository
import com.msukno.gameapprawg.model.GenreImage
import com.msukno.gameapprawg.utils.HelperFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "GenreCacheWorker"

/**
 * Worker class for caching genre background images.
 */
class GenreCacheWorker(
    val genreRepository: GenreRepository,
    val genreImageRepository: GenreImageRepository,
    val imageDownloader: ImageDownloader,
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {


        return withContext(Dispatchers.IO){
            return@withContext try {

                val genres = genreRepository.getAll()
                val imgsToCache: MutableList<GenreImage> = mutableListOf()
                val timeNow = HelperFunctions.dateTimeByDay()
                val cachedImages = genreImageRepository.getImagesWithIds(genres.map { it.id })
                val cachedIds = cachedImages.map { it.genreId }
                val idsExpired = cachedImages.filter { it.refreshTime != timeNow }.map { it.genreId }
                val newIds = genres.filter { it.id !in cachedIds }.map { it.id }
                val toCacheIds = idsExpired + newIds

                genres.forEach { genre ->
                    try {
                        if (genre.id in toCacheIds){
                            val path = imageDownloader.downloadAndSaveImage(
                                genre.imageBackground, IMG_CACHE_DIR, "genre-${genre.id}.png"
                            )
                            imgsToCache.add(GenreImage(genre.id, path, timeNow))
                        }
                    } catch (throwable: Throwable){
                        Log.e(TAG, "Error while fetching image:", throwable)
                    }
                }
                genreImageRepository.insertAll(imgsToCache)
                Result.success()
            }catch (throwable: Throwable){
                Log.e(TAG, "Error while cache update", throwable)
                Result.failure()
            }
        }
    }
}