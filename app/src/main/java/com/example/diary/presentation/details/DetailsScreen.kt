package com.example.diary.presentation.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diary.domain.model.Mood
import com.example.diary.presentation.details.components.DetailsContent
import com.example.diary.presentation.details.components.DetailsTopAppBar
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    snackbarHostState: SnackbarHostState,
    selectedDiaryId: String?,
    pagerState: PagerState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMoodChange: (Mood) -> Unit,
    onSaveOrUpdateClick: () -> Unit,
    onDateTimeUpdate: (ZonedDateTime) -> Unit,
    onDeleteClick: () -> Unit
) {

    LaunchedEffect(key1 = state.mood) {
        pagerState.scrollToPage(page = Mood.valueOf(state.mood.name).ordinal)
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
            SnackbarHost(hostState = snackbarHostState) {
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
            pagerState = pagerState,
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
            selectedDiaryId = selectedDiaryId
        )
    }
}
