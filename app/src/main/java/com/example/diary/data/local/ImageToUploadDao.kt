package com.example.diary.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diary.data.local.entity.ImageToUpload

@Dao
interface ImageToUploadDao {

    @Query("SELECT * FROM images_to_upload ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUpload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToUpload(imageToUpload: ImageToUpload)

    @Query("DELETE FROM images_to_upload WHERE id = :imageId")
    suspend fun cleanupImage(imageId:Int)
}