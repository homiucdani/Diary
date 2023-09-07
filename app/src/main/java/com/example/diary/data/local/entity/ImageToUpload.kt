package com.example.diary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.diary.util.Constants

@Entity(tableName = Constants.IMAGES_TO_UPLOAD_TABLE)
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
