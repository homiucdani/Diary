package com.example.home

import com.example.util.connectivity.ConnectivityObserver
import com.example.ui.util.MessageBarUi
import com.example.util.model.Diary
import java.time.LocalDate

data class HomeState(
    val diaries: Map<LocalDate, List<Diary>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val messageBarUi: MessageBarUi = MessageBarUi(),
    val network: ConnectivityObserver.Status = ConnectivityObserver.Status.Unavailable,
    val filterSelectedDate: LocalDate? = null
)
