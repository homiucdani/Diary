package com.example.diary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.diary.data.local.entity.ImageToDelete
import com.example.diary.data.local.entity.ImageToUpload

@Database(
    entities = [
        ImageToUpload::class,
        ImageToDelete::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}