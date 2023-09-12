package com.example.mongo.mapper

import com.example.mongo.local.entity.ImageToUploadEntity
import com.example.util.model.ImageToUpload

fun ImageToUpload.toImageToUploadEntity(): ImageToUploadEntity {
    return ImageToUploadEntity(
        id = id,
        remoteImagePath = remoteImagePath,
        imageUri = imageUri,
        sessionUri = sessionUri
    )
}

fun ImageToUploadEntity.toImageToUpload(): ImageToUpload {
    return ImageToUpload(
        id = id,
        remoteImagePath = remoteImagePath,
        imageUri = imageUri,
        sessionUri = sessionUri
    )
}