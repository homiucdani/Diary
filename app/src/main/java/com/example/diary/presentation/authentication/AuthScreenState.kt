package com.example.diary.presentation.authentication

import com.example.diary.core.presentation.util.MessageBarUi

data class AuthScreenState(
    val isLoading: Boolean = false,
    val messageBarUi: MessageBarUi = MessageBarUi(),
    val isAuthenticated: Boolean = false
)
