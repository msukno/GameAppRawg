package com.msukno.gameapprawg.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Provides a method for downloading an image from URL and saving it to the internal storage
 */
interface ImageDownloader {
    suspend fun downloadAndSaveImage(url: String, saveDir: String, filename: String): String
}

private const val TAG = "CoilImageDownloader"
class CoilImageDownloader(val context: Context) : ImageDownloader{

    override suspend fun downloadAndSaveImage(url: String, saveDir: String, filename: String): String{
        val imageLoader = ImageLoader(context)
        val directory = context.getDir(saveDir, Context.MODE_PRIVATE)
        val file = File(directory, filename)
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(onSuccess = { drawable ->
                CoroutineScope(Dispatchers.IO).launch {
                    drawable.toBitmapOrNull()?.let { bitmap ->
                        FileOutputStream(file).use { stream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        }
                    }
                }
            })
            .build()
        imageLoader.enqueue(request)
        return file.absolutePath
    }
}