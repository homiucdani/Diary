package com.example.auth

import com.example.ui.util.MessageBarUi

data class AuthScreenState(
    val isLoading: Boolean = false,
    val messageBarUi: MessageBarUi = MessageBarUi(),
    val isAuthenticated: Boolean = false
)
