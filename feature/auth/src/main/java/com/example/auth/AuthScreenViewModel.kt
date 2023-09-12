package com.example.auth

import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.util.MessageBarUi
import com.example.util.Constants
import com.example.util.Constants.APP_ID
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthScreenViewModel : ViewModel() {

    private val app = App.create(APP_ID)
    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    fun addErrorOrMessage(message: String? = null, error: Exception? = null) {
        _state.update {
            it.copy(
                messageBarUi = it.messageBarUi.copy(message = message, exception = error),
                isLoading = false
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


    fun signInWithFirebase(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val credential = GoogleAuthProvider.getCredential(token, null)
            val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()

            if (result != null) {
                signInWithMongoAtlas(token)
            } else {
                withContext(Dispatchers.Main){
                    _state.update {
                        it.copy(
                            messageBarUi = MessageBarUi(
                                exception = Exception("Sign in failed."),
                            ),
                            isAuthenticated = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun signInWithMongoAtlas(
        token: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = app
                    .login(
                        Credentials.jwt(
                            jwtToken = token
                        )
                    ).loggedIn

                withContext(Dispatchers.Main) {
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