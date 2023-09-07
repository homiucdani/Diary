package com.example.diary.presentation.details

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diary.data.local.ImageToDeleteDao
import com.example.diary.data.local.ImageToUploadDao
import com.example.diary.data.local.entity.ImageToDelete
import com.example.diary.data.local.entity.ImageToUpload
import com.example.diary.data.remote.MongoRepositoryImpl
import com.example.diary.domain.model.Diary
import com.example.diary.domain.model.GalleryImage
import com.example.diary.domain.model.Mood
import com.example.diary.util.RequestState
import com.example.diary.util.fetchImagesFromFirebase
import com.example.diary.util.toInstant
import com.example.diary.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    private val _galleryImages = mutableStateListOf<GalleryImage>()
    private val _galleryImagesToBeDeleted = mutableStateListOf<GalleryImage>()

    val galleryImages: List<GalleryImage> = _galleryImages
    val galleryImagesToBeDeleted: List<GalleryImage> = _galleryImagesToBeDeleted

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeDiaryId()
    }

    fun onTitleChange(title: String) {
        _state.update {
            it.copy(
                title = title
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _state.update {
            it.copy(
                description = description
            )
        }
    }

    fun onMoodChange(mood: Mood) {
        _state.update {
            it.copy(
                mood = mood
            )
        }
    }

    private fun observeDiaryId() {
        val diaryId = savedStateHandle.get<String>("diaryId")
        if (diaryId != null) {
            viewModelScope.launch {
                MongoRepositoryImpl.getDiaryById(ObjectId.invoke(diaryId))
                    .catch {
                        emit(RequestState.Error(Exception("Diary does not exist.")))
                    }
                    .collect { result ->
                        when (result) {
                            is RequestState.Success -> {

                                _state.update {
                                    it.copy(
                                        diaryId = diaryId,
                                        title = result.data.title,
                                        description = result.data.description,
                                        mood = Mood.valueOf(result.data.mood),
                                        images = result.data.images,
                                        date = result.data.date.toInstant(),
                                    )
                                }

                                fetchImagesFromFirebase(
                                    remoteImagePaths = result.data.images,
                                    onImageDownload = { imageUri ->
                                        _galleryImages.add(
                                            GalleryImage(
                                                image = imageUri,
                                                remoteImagePath = extractRemoteImagePath(imageUri.toString())
                                            )
                                        )
                                    },
                                    onImageDownloadFailed = { exception ->
                                        _state.update {
                                            it.copy(
                                                messageBarUi = it.messageBarUi.copy(
                                                    exception = exception
                                                )
                                            )
                                        }
                                    }
                                )
                            }

                            is RequestState.Error -> {
                                _state.update {
                                    it.copy(
                                        messageBarUi = it.messageBarUi.copy(
                                            exception = result.data as Exception
                                        )
                                    )
                                }
                            }

                            else -> Unit
                        }
                    }
            }
        }
    }

    fun insertDiaryIntoDb() {
        if (
            state.value.title.isNotEmpty() && state.value.description.isNotEmpty()
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = MongoRepositoryImpl.insertDiary(
                    Diary().apply {
                        title = state.value.title
                        description = state.value.description
                        mood = state.value.mood.name
                        date = state.value.date.toRealmInstant()
                        images = galleryImages.map { it.remoteImagePath }.toRealmList()
                    }
                )
                when (result) {
                    is RequestState.Success -> {
                        uploadImagesToFirebase()
                    }

                    is RequestState.Error -> {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    messageBarUi = it.messageBarUi.copy(
                                        exception = result.data as Exception
                                    ),
                                    isUploadingImages = false
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        } else {
            viewModelScope.launch {
                _uiEvent.emit("Title or description is empty.")
            }
        }
    }

    fun updateDiaryIntoDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MongoRepositoryImpl.updateDiary(
                Diary().apply {
                    _id = ObjectId.invoke(state.value.diaryId)
                    title = state.value.title
                    description = state.value.description
                    mood = state.value.mood.name
                    date = state.value.date.toRealmInstant()
                    images = state.value.images
                }
            )
            when (result) {
                is RequestState.Success -> {
                    uploadImagesToFirebase()
                }

                is RequestState.Error -> {
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(
                                messageBarUi = it.messageBarUi.copy(
                                    exception = result.data as Exception
                                )
                            )
                        }
                    }
                }

                else -> Unit
            }
        }
    }

    fun deleteDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MongoRepositoryImpl.deleteDiary(ObjectId.invoke(state.value.diaryId))
            withContext(Dispatchers.Main) {
                when (result) {
                    is RequestState.Success -> {
                        _state.update {
                            it.copy(
                                messageBarUi = it.messageBarUi.copy(
                                    message = result.data
                                )
                            )
                        }
                        if (state.value.images.isNotEmpty()) {
                            deleteImagesFromFirebase(images = state.value.images)
                        }
                    }

                    is RequestState.Error -> {
                        _state.update {
                            it.copy(
                                messageBarUi = it.messageBarUi.copy(
                                    exception = result.data as Exception
                                )
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        _state.update {
            it.copy(
                date = zonedDateTime.toInstant()
            )
        }
    }

    fun addImage(image: Uri, imageType: String) {
        // images/currentUser/imageName-currentTime.jpg
        val remoteImagePath =
            "images/${FirebaseAuth.getInstance().currentUser?.uid}" +
                    "/${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"

        addImageToGalleryList(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    fun scheduleRemove(image: GalleryImage) {
        _galleryImages.remove(image)
        _galleryImagesToBeDeleted.add(image)
    }

    private fun deleteImagesFromFirebase(images: List<String>) {
        val storage = FirebaseStorage.getInstance().reference
        images.forEach { remotePath ->
            storage.child(remotePath).delete()
                .addOnFailureListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        imageToDeleteDao.addImageToDelete(
                            ImageToDelete(
                                remoteImagePath = remotePath
                            )
                        )
                    }
                }
        }
    }

    private fun uploadImagesToFirebase() {
        deleteImagesFromFirebase(images = galleryImagesToBeDeleted.map { it.remoteImagePath })
        _state.update {
            it.copy(
                isUploadingImages = true
            )
        }
        val storage = FirebaseStorage.getInstance().reference
        galleryImages.forEach { image ->
            val imagePath = storage.child(image.remoteImagePath)
            imagePath.putFile(image.image)
                .addOnProgressListener { snapshot ->
                    val sessionUri = snapshot.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = image.remoteImagePath,
                                    imageUri = image.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }.addOnSuccessListener {
                    _state.update {
                        it.copy(
                            isUploadingImages = false
                        )
                    }
                }
        }
    }

    private fun extractRemoteImagePath(imageUrl: String): String {
        val chunks = imageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }

    private fun addImageToGalleryList(image: GalleryImage) {
        _galleryImages.add(image)
    }
}