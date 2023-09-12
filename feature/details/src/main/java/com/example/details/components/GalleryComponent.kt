package com.example.details.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.model.GalleryImage
import kotlin.math.max
import com.example.ui.R

@Composable
fun GalleryComponent(
    modifier: Modifier = Modifier,
    galleryImages: () -> List<GalleryImage>,
    imageSize: Dp = 55.dp,
    spaceBetween: Dp = 12.dp,
    imageShape: CornerBasedShape = Shapes().medium,
    onImageToSave: (Uri) -> Unit,
    onImageClick: (GalleryImage) -> Unit
) {

    val context = LocalContext.current

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
        onResult = { imagesResult ->
            imagesResult.forEach { image ->
                onImageToSave(image)
            }
        }
    )

    BoxWithConstraints(modifier = modifier) {
        val numberOfImageVisible = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = maxWidth.div(imageSize + spaceBetween).toInt().minus(2)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                galleryImages().size - numberOfImageVisible.value
            }
        }

        Row {
            AddImageButton(
                onAddImageClick = {
                    multiplePhotoPicker.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                imageShape = imageShape,
                imageSize = imageSize
            )

            Spacer(modifier = Modifier.width(spaceBetween))

            galleryImages().take(numberOfImageVisible.value).forEach { galleryImage ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(galleryImage.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.gallery_image),
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize)
                        .clickable {
                            onImageClick(galleryImage)
                        },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(spaceBetween))
            }

            if (remainingImages.value > 0) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier
                            .clip(imageShape)
                            .size(imageSize),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {}
                    Text(
                        text = "+${remainingImages.value}",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun AddImageButton(
    onAddImageClick: () -> Unit,
    imageShape: CornerBasedShape,
    imageSize: Dp
) {
    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onAddImageClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .clip(imageShape)
                .size(imageSize),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        Text(
            text = "+",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}