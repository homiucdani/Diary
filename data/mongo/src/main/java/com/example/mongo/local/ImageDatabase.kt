package com.example.mongo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mongo.local.entity.ImageToDeleteEntity
import com.example.mongo.local.entity.ImageToUploadEntity

@Database(
    entities = [
        ImageToUploadEntity::class,
        ImageToDeleteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}