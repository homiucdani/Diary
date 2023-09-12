package com.example.mongo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mongo.local.entity.ImageToDeleteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageToDeleteDao {

    @Query("SELECT * FROM images_to_delete ORDER BY id ASC")
    fun getAllImagesToDelete(): Flow<List<ImageToDeleteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToDelete(imageToDeleteEntity: ImageToDeleteEntity)

    @Query("DELETE FROM images_to_delete WHERE id = :imageId")
    suspend fun cleanupImages(imageId: Int)
}