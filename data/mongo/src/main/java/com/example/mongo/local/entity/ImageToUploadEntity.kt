package com.example.mongo.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.Constants

@Entity(tableName = Constants.IMAGES_TO_UPLOAD_TABLE)
data class ImageToUploadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
