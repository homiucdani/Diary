package com.example.mongo.mapper

import com.example.mongo.local.entity.ImageToDeleteEntity
import com.example.util.model.ImageToDelete

fun ImageToDelete.toImageToDeleteEntity(): ImageToDeleteEntity {
    return ImageToDeleteEntity(
        id = id,
        remoteImagePath = remoteImagePath
    )
}

fun ImageToDeleteEntity.toImageToDelete(): ImageToDelete {
    return ImageToDelete(
        id = id,
        remoteImagePath = remoteImagePath
    )
}