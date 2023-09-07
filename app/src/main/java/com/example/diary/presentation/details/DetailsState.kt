package com.example.diary.presentation.details

import com.example.diary.core.presentation.util.MessageBarUi
import com.example.diary.domain.model.GalleryImage
import com.example.diary.domain.model.Mood
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import java.time.Instant

data class DetailsState(
    val diaryId: String = "",
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val date: Instant = Instant.now(),
    val images: RealmList<String> = realmListOf(),
    val messageBarUi: MessageBarUi = MessageBarUi(),
    val galleryImages: MutableList<GalleryImage> = mutableListOf(),
    val galleryImagesToBeDeleted: MutableList<GalleryImage> = mutableListOf(),
    val isUploadingImages:Boolean = false
)
