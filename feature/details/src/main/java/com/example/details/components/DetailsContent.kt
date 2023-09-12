package com.example.details.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.model.GalleryImage
import com.example.util.model.Mood
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedDiaryId: String?,
    title: () -> String,
    onTitleChange: (String) -> Unit,
    description: () -> String,
    onDescriptionChange: (String) -> Unit,
    onMoodChange: (Mood) -> Unit,
    onSaveOrUpdateClick: () -> Unit,
    galleryImages: () -> List<GalleryImage>,
    onImageToSave: (Uri) -> Unit,
    isUploadingImages: () -> Boolean,
    onImageClick: (GalleryImage) -> Unit
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = pagerState.isScrollInProgress) {
        onMoodChange(Mood.values()[pagerState.currentPage])
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            HorizontalPager(
                state = pagerState,
                pageCount = Mood.values().size
            ) { pageNumber ->
                AsyncImage(
                    modifier = Modifier.size(120.dp),
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(Mood.values()[pageNumber].icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${Mood.values()[pageNumber].name} icon"
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            ReusableInputField(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                onTextChange = onTitleChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        coroutineScope.launch {
                            scrollState.scrollTo(Int.MAX_VALUE)
                        }
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                ),
                placeholder = "Title"
            )

            ReusableInputField(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                onTextChange = onDescriptionChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                ),
                placeholder = "Tell me about it."
            )
        }

        Column(
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            GalleryComponent(
                galleryImages = galleryImages,
                onImageToSave = onImageToSave,
                onImageClick = onImageClick
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                onClick = {
                    onSaveOrUpdateClick()
                },
                shape = Shapes().small
            ) {
                if (isUploadingImages()) {
                    CircularProgressIndicator()
                } else {
                    Text(text = if (selectedDiaryId == null) "Save" else "Update")
                }
            }
        }
    }
}

@Composable
private fun ReusableInputField(
    modifier: Modifier = Modifier,
    text: () -> String,
    onTextChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    colors: TextFieldColors,
    placeholder: String
) {
    TextField(
        modifier = modifier,
        value = text(),
        onValueChange = onTextChange,
        placeholder = {
            Text(text = placeholder)
        },
        colors = colors,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        singleLine = singleLine
    )
}