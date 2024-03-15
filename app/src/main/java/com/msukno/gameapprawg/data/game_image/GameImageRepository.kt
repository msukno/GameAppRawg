package com.msukno.gameapprawg.data.game_image

import com.msukno.gameapprawg.model.GameFavorite
import com.msukno.gameapprawg.model.GameImages
import kotlinx.coroutines.flow.Flow

/**
 * Repository provides methods for inserting, deleting and retrieving of [GameImages] from
 * local database which is used for caching of paths to each game image.
 */

interface GameImageRepository {
    fun getAllImagesStream(): Flow<List<GameImages>>
    suspend fun insertAll(images: List<GameImages>)
    suspend fun getImagesWithIds(ids: List<Int>): List<GameImages>
    suspend fun getAll(): List<GameImages>
    suspend fun clearCache()
}

class LocalGameImageRepository(val gameImageDao: GameImageDao): GameImageRepository{
    override fun getAllImagesStream(): Flow<List<GameImages>> = gameImageDao.getAllImages()

    override suspend fun insertAll(images: List<GameImages>) = gameImageDao.insertAll(images)

    override suspend fun getImagesWithIds(ids: List<Int>): List<GameImages> = gameImageDao.getImagesWithIds(ids)

    override suspend fun getAll(): List<GameImages> = gameImageDao.getAll()

    override suspend fun clearCache() = gameImageDao.clearAll()
}