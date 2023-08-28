package com.example.diary.presentation.authentication

import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diary.util.Constants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val mongoApp: App
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    fun addErrorOrMessage(message: String? = null, error: Exception? = null) {
        _state.update {
            it.copy(
                messageBarUi = it.messageBarUi.copy(message = message, exception = error)
            )
        }
    }

    fun onSignInClick(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun signInWithMongoAtlas(
        token: String
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    mongoApp
                        .login(
                            Credentials.jwt(
                                jwtToken = token
                            )
                        ).loggedIn
                }
                if (result) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            messageBarUi = it.messageBarUi.copy(
                                message = "Successfully Authenticated.",
                                exception = null
                            ),
                            isAuthenticated = true
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isLoading = false,
                        messageBarUi = it.messageBarUi.copy(exception = e, message = null)
                    )
                }
            }
        }
    }

    fun requestOneTap(
        activity: Activity,
        launch: (IntentSenderRequest) -> Unit
    ) {
        val signInClient = Identity.getSignInClient(activity)
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(Constants.CLIEND_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("SignIn", "signIn: Couldn't start one tap Ui ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Log.d("SignIn", "signIn: Something went wrong ${exception.message}")
            }
    }
}