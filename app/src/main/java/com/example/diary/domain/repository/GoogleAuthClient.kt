package com.example.diary.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest

interface GoogleAuthClient {
    suspend fun signInClient(): IntentSender?

    fun signInWithIntent(intent: Intent): String?

    fun buildSignInRequest(): BeginSignInRequest
}