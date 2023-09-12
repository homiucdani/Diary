package com.example.mongo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mongo.local.entity.ImageToUploadEntity

@Dao
interface ImageToUploadDao {

    @Query("SELECT * FROM images_to_upload ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUploadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToUpload(imageToUploadEntity: ImageToUploadEntity)

    @Query("DELETE FROM images_to_upload WHERE id = :imageId")
    suspend fun cleanupImage(imageId:Int)
}