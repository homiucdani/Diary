package com.example.diary.presentation.home

import com.example.diary.core.presentation.util.MessageBarUi
import com.example.diary.domain.model.Diary
import java.time.LocalDate

data class HomeState(
    val diaries: Map<LocalDate, List<Diary>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val messageBarUi: MessageBarUi = MessageBarUi(),
)
