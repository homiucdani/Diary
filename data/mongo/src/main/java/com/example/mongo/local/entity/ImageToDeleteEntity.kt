package com.example.mongo.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.Constants

@Entity(tableName = Constants.IMAGES_TO_DELETE_TABLE)
data class ImageToDeleteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String
)
