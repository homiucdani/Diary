package com.example.diary.presentation.details

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diary.domain.model.GalleryImage
import com.example.diary.domain.model.Mood
import com.example.diary.presentation.details.components.DetailsContent
import com.example.diary.presentation.details.components.DetailsTopAppBar
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    snackbarHostState: () -> SnackbarHostState,
    selectedDiaryId: String?,
    pagerState: () -> PagerState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMoodChange: (Mood) -> Unit,
    onSaveOrUpdateClick: () -> Unit,
    onDateTimeUpdate: (ZonedDateTime) -> Unit,
    onDeleteClick: () -> Unit,
    onImageSelect: (Uri) -> Unit,
    galleryImages: () -> List<GalleryImage>,
    onDeleteImageClick: (GalleryImage) -> Unit
) {

    var selectedGalleryImage by remember {
        mutableStateOf<GalleryImage?>(null)
    }

    LaunchedEffect(key1 = state.mood) {
        pagerState().scrollToPage(page = Mood.valueOf(state.mood.name).ordinal)
    }

    Scaffold(
        topBar = {
            DetailsTopAppBar(
                onBackClick = onBackClick,
                selectedDiaryId = selectedDiaryId,
                onDeleteClick = onDeleteClick,
                moodName = {
                    state.mood.name
                },
                diaryTitle = {
                    state.title
                },
                diaryTime = {
                    state.date
                },
                onDateTimeUpdate = { zonedDateTime ->
                    onDateTimeUpdate(zonedDateTime)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState()) {
                Snackbar(snackbarData = it)
            }
        }
    ) { paddingValues ->
        DetailsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(bottom = 24.dp)
                .padding(horizontal = 24.dp),
            pagerState = pagerState(),
            title = {
                state.title
            },
            onTitleChange = onTitleChange,
            description = {
                state.description
            },
            onDescriptionChange = onDescriptionChange,
            onMoodChange = onMoodChange,
            onSaveOrUpdateClick = onSaveOrUpdateClick,
            selectedDiaryId = selectedDiaryId,
            onImageSelect = onImageSelect,
            galleryImages = galleryImages,
            isUploadingImages = {
                state.isUploadingImages
            },
            onImageClick = { galleryImage ->
                selectedGalleryImage = galleryImage
            }
        )

        AnimatedVisibility(visible = selectedGalleryImage != null) {
            Dialog(
                onDismissRequest = {
                    selectedGalleryImage = null
                }
            ) {
                selectedGalleryImage?.let { galleryImage ->
                    ZoomableImage(
                        selectedGalleryImage = galleryImage,
                        onCloseClicked = {
                            selectedGalleryImage = null
                        },
                        onDeleteClicked = {
                            onDeleteImageClick(galleryImage)
                            selectedGalleryImage = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    selectedGalleryImage: GalleryImage,
    onCloseClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val context = LocalContext.current
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(1f, minOf(scale * zoom, 5f))
                    val maxX = (size.width * (scale - 1)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                    val maxY = (size.height * (scale - 1)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale)),
                    scaleY = maxOf(.5f, minOf(3f, scale)),
                    translationX = offsetX,
                    translationY = offsetY
                ),
            model = ImageRequest.Builder(context)
                .data(selectedGalleryImage.image.toString())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = "Gallery Image"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onCloseClicked) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                Text(text = "Close")
            }
            Button(onClick = onDeleteClicked) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                Text(text = "Delete")
            }
        }
    }
}
