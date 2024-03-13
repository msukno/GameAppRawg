package com.msukno.gameapprawg.data.genre_image

import com.msukno.gameapprawg.model.GameImages
import com.msukno.gameapprawg.model.GenreImage
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides methods for inserting and retrieving of [GenreImage] from
 * local database which is used for caching of paths to each genre image.
 */
interface GenreImageRepository {
    fun getAllImagesStream(): Flow<List<GenreImage>>
    suspend fun insertAll(images: List<GenreImage>)
    suspend fun getImagesWithIds(ids: List<Int>): List<GenreImage>
}

class LocalGenreImageRepository(private val genreImageDao: GenreImageDao): GenreImageRepository {

    override fun getAllImagesStream(): Flow<List<GenreImage>> = genreImageDao.getAllImages()

    override suspend fun insertAll(images: List<GenreImage>) = genreImageDao.insertAll(images)

    override suspend fun getImagesWithIds(ids: List<Int>): List<GenreImage> = genreImageDao.getImagesWithIds(ids)
}