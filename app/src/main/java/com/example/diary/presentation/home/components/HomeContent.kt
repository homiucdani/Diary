package com.example.diary.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diary.R
import com.example.diary.domain.model.Diary
import com.example.diary.domain.model.Mood
import com.example.diary.ui.theme.Elevation
import com.example.diary.util.toInstant
import com.example.diary.util.toStringTime
import io.realm.kotlin.ext.realmListOf
import java.time.Instant
import java.time.LocalDate
import kotlin.math.max


@Composable
fun HomeContent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DateHeader(localDate = LocalDate.now())
        DiaryHolder(diary = Diary().apply {
            title = "Some title"
            description =
                "This is some random text, to test the new functionality that we added, so stay tuned for new updates on this."
            mood = Mood.Happy.name
            images = realmListOf("", "")
        }, onDiaryClick = {})
    }
}

@Composable
fun DateHeader(
    localDate: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = localDate.dayOfMonth.toString(),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )

            Text(
                text = localDate.dayOfWeek.toString().take(3),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = localDate.month.toString().lowercase().replaceFirstChar { it.titlecase() }
                    .take(3),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )

            Text(
                text = localDate.year.toString(),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Composable
fun DiaryHolder(
    diary: Diary,
    onDiaryClick: (String) -> Unit
) {
    var componentHeight by remember {
        mutableStateOf(0.dp)
    }

    val localDensity = LocalDensity.current

    var expandGallery by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            // remove ripple effect, affects the left line not good design experience
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onDiaryClick(diary._id.toHexString())
            }
    ) {

        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = Elevation.Level2
        ) {}
        Spacer(modifier = Modifier.width(14.dp))

        Surface(
            modifier = Modifier.onGloballyPositioned {
                componentHeight = with(localDensity) { it.size.height.toDp() }
            },
            shape = RoundedCornerShape(12.dp),
            tonalElevation = Elevation.Level1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DiaryHeader(moodName = diary.mood, time = diary.date.toInstant())
                Text(
                    text = diary.description,
                    modifier = Modifier.padding(14.dp),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    ),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                if (diary.images.isNotEmpty()) {
                    ShowGalleryButton(
                        expandGallery = expandGallery
                    ) {
                        expandGallery = !expandGallery
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier.padding(14.dp),
                    visible = expandGallery
                ) {
                    ExpandableGallery(
                        images = diary.images
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryHeader(
    moodName: String, time: Instant
) {
    val mood by remember {
        mutableStateOf(Mood.valueOf(moodName))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(mood.containerColor)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = mood.icon),
                contentDescription = moodName,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = mood.name,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Bold
                ),
                color = mood.contentColor
            )
        }

        Text(
            text = time.toStringTime(),
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Bold
            ),
            color = mood.contentColor
        )
    }
}

@Composable
fun ExpandableGallery(
    modifier: Modifier = Modifier,
    images: List<String>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: CornerBasedShape = Shapes().small,
) {
    //imageSize + spaceBetween = 50 -> represents one item in the row
    // assume the row has a width of 300 / 50 = 6, we got 6 items in the row, -1 are the others images that cannot be saved

    BoxWithConstraints(modifier = modifier) {
        // the derived state will not cause the calculation to be recalculated cuz we are inside a composable and it can recompose
        // 20 times and we dont wanna recalculate those values, only if the values inside change

        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = maxWidth.div(imageSize + spaceBetween).toInt().minus(1)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                images.size - numberOfVisibleImages.value
            }
        }

        Row {
            images.take(numberOfVisibleImages.value).forEach { image ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.gallery_image),
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize)
                )
                Spacer(modifier = Modifier.width(spaceBetween))
            }
            if (remainingImages.value > 0) {
                LastImageCountHolder(
                    imageSize = imageSize,
                    remainingImages = remainingImages.value,
                    imageShape = imageShape
                )
            }
        }
    }
}

@Composable
fun LastImageCountHolder(
    imageSize: Dp,
    remainingImages: Int,
    imageShape: CornerBasedShape
) {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .clip(imageShape)
                .size(imageSize),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        Text(
            text = "+${remainingImages}",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ShowGalleryButton(
    expandGallery: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = if (expandGallery) "Hide Gallery" else "Show Gallery",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DateHeaderPreview() {
    MaterialTheme {
        Column {

        }
    }
}