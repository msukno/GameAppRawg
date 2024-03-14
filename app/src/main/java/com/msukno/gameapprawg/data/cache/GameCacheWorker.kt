package com.msukno.gameapprawg.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.msukno.gameapprawg.data.AppParamManager
import com.msukno.gameapprawg.data.game.GameRepository
import com.msukno.gameapprawg.data.game_favorite.GameFavoriteRepository
import com.msukno.gameapprawg.data.game_image.GameImageRepository
import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.toGame
import com.msukno.gameapprawg.ui.screens.game_details.GameLocation
import com.msukno.gameapprawg.ui.screens.game_list.ImageType
import com.msukno.gameapprawg.utils.HelperFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TAG = "GameCacheWorker"
private const val MIN_IMAGE_SIZE = 896

/**
 * Worker class for caching game images
 */
class GameCacheWorker(
    val gameRepository: GameRepository,
    val gameFavoriteRepository: GameFavoriteRepository,
    val gameImageRepository: GameImageRepository,
    val paramManager: AppParamManager,
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {

        val ids  = inputData.getIntArray("IDs") ?: return Result.failure()
        val typeParam  = inputData.getString("imageType") ?: return Result.failure()
        val locationParam  = inputData.getString("gameLocation") ?: return Result.failure()
        val imageType = ImageType.valueOf(typeParam)
        val gameLocation = GameLocation.valueOf(locationParam)
        val idList = ids.map { it }


        return withContext(Dispatchers.IO){
            return@withContext try {
                //Get games for provided IDs from GameRepository or GameFavoriteRepository
                val games = if(gameLocation == GameLocation.games_table) gameRepository.findGamesByIds(idList)
                else gameFavoriteRepository.findGamesByIds(idList).map { it.toGame() }
                val imgsToCache: MutableList<GameImages> = mutableListOf()
                val timeNow = HelperFunctions.dateTimeByDay()
                //Create a placeholder by creating a new or updating the existing GameImage object
                val imgsPlaceholder = createPlaceholderForImages(idList, imageType, timeNow)
                //Download and cache images from the web. Also update the placeholders based on image type
                games.forEach { game ->
                    imgsPlaceholder[game.id]?.let { placeholder ->
                        if(imageType == ImageType.background){
                            val backgroundImage = downloadImage(url = game.backgroundImage)
                            backgroundImage?.let { bitmap ->
                                val path = saveImage(bitmap, "gameBackground-${game.id}.jpeg")
                                path?.let { filepath ->
                                    imgsToCache.add(placeholder.copy(background = filepath, backRefreshTime = timeNow))
                                }
                            }
                        }else{
                            val screenshots: MutableSet<String> = mutableSetOf()
                            val screenshotBitmaps = game.screenshots.mapNotNull{ downloadImage(url = it) }
                            screenshotBitmaps.forEachIndexed{ index, bitmap ->
                                val path = saveImage(bitmap, "gameScreenShot-${game.id}-${index+1}.jpeg")
                                path?.let { filepath ->
                                    screenshots.add(filepath)
                                }
                            }
                            imgsToCache.add(placeholder.copy(screenshots = screenshots, screenRefreshTime = timeNow))
                        }
                    }
                }

                gameImageRepository.insertAll(imgsToCache.toList())
                Result.success()
            }catch (throwable: Throwable){
                Log.e(TAG, "Error while cache update", throwable)
                Result.failure()
            }
        }
    }

    //Create placeholders for images to be cached. We update the images only if the refresh time
    //is different then the current time. By default this means we cache images only if they are new
    //or 24 hours has passed since last cache
    private suspend fun createPlaceholderForImages(
        ids: List<Int>,
        imageType: ImageType,
        timeNow: String
    ): Map<Int, GameImages>{
        val imgPlaceholders: MutableMap<Int, GameImages> = mutableMapOf()
        val cachedImages = gameImageRepository.getImagesWithIds(ids)
        val cachedIds = cachedImages.map { it.gameId}

        val imgsExpired = if (imageType == ImageType.background)
            cachedImages.filter { it.backRefreshTime != timeNow }
        else cachedImages.filter { it.screenRefreshTime != timeNow }

        val newIds = ids.filter { it !in cachedIds }
        imgsExpired.forEach { imgPlaceholders[it.gameId] = it }
        newIds.forEach { imgPlaceholders[it] = GameImages(gameId = it) }

        return imgPlaceholders
    }

    private suspend fun downloadImage(url: String): Bitmap?{
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()

        return (imageLoader.execute(request).drawable as? BitmapDrawable)?.bitmap
    }

    private fun saveImage(bitmap: Bitmap, filename: String): String? {
        return try {
            // all images are resized to width = MIN_IMAGE_SIZE
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap, MIN_IMAGE_SIZE, MIN_IMAGE_SIZE * bitmap.height / bitmap.width, true
            )
            val directory = context.getDir(IMG_CACHE_DIR, Context.MODE_PRIVATE)
            val file = File(directory, filename)
            FileOutputStream(file).use { stream ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }
            file.absolutePath
        }catch (throwable: Throwable){
            Log.d(TAG, "error while writing image: ${throwable.message}")
            null
        }
    }
}