package com.example.details.navigation

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.details.DetailsScreen
import com.example.details.DetailsViewModel
import com.example.util.Screen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.detailsRoute(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    popOnDelete: () -> Unit
) {
    composable(
        route = Screen.Details.route,
        arguments = listOf(
            navArgument(name = "diaryId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { args ->
        val detailsViewModel: DetailsViewModel = hiltViewModel()
        val state = detailsViewModel.state.collectAsState().value
        val galleryImageState = detailsViewModel.galleryImages

        val pagerState = rememberPagerState()
        val diaryId = args.arguments?.getString("diaryId")

        val context = LocalContext.current

        LaunchedEffect(key1 = detailsViewModel.uiEvent) {
            detailsViewModel.uiEvent.collectLatest { message ->
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }

        DetailsScreen(
            state = state,
            onBackClick = onBackClick,
            pagerState = {
                pagerState
            },
            onTitleChange = { title ->
                detailsViewModel.onTitleChange(title)
            },
            onDescriptionChange = { description ->
                detailsViewModel.onDescriptionChange(description)
            },
            onMoodChange = { mood ->
                detailsViewModel.onMoodChange(mood)
            },
            selectedDiaryId = diaryId,
            onSaveOrUpdateClick = {
                if (diaryId == null) {
                    detailsViewModel.insertDiaryIntoDb()
                    if (!state.isUploadingImages) {
                        onBackClick()
                    }
                } else {
                    detailsViewModel.updateDiaryIntoDb()
                    if (!state.isUploadingImages) {
                        onBackClick()
                    }
                }
            },
            snackbarHostState = { snackbarHostState },
            onDateTimeUpdate = { zonedDateTime ->
                detailsViewModel.updateDateTime(zonedDateTime)
            },
            onDeleteClick = {
                detailsViewModel.deleteDiary().also { popOnDelete() }
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            },
            galleryImages = {
                galleryImageState
            },
            onImageToSave = { imageUri ->
                //image '/' jpeg
                val type = context.contentResolver.getType(imageUri)?.split("/")?.last() ?: "jpg"
                detailsViewModel.addImage(
                    image = imageUri,
                    imageType = type
                )
            },
            onDeleteImageClick = { galleryImage ->
                detailsViewModel.scheduleRemove(galleryImage)
            }
        )
    }
}