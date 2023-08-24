package com.example.diary.presentation.authentication

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.diary.util.Constants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

@Composable
fun GoogleOneTap(
    key: Any,
    launch: (ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit,
    onResultReceived: (String) -> Unit,
    onDismissDialog: (String?) -> Unit
) {

    val activity = LocalContext.current as Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { activityResult ->
            try {
                if (activityResult.resultCode == RESULT_OK) {
                    val oneTapSignIn = Identity.getSignInClient(activity)
                    val credentials =
                        oneTapSignIn.getSignInCredentialFromIntent(activityResult.data)
                    val googleToken = credentials.googleIdToken
                    if (googleToken != null) {
                        onResultReceived(googleToken)
                    }
                } else {
                    Log.d("GoogleOneTap", "GoogleOneTap: Google one tap closed")
                    onDismissDialog("Dialog Closed")
                }
            } catch (apiException: ApiException) {
                when (apiException.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d("GoogleOneTap", "GoogleOneTap: Google one tap cancelled")
                    }

                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.d("GoogleOneTap", "GoogleOneTap: ONE TAP NETWORK ERROR")
                    }

                    else -> {
                        Log.d("GoogleOneTap", "GoogleOneTap: ${apiException.message}")
                        onDismissDialog(apiException.message)
                    }
                }
            }
        }
    )

    LaunchedEffect(key1 = key) {
        launch(launcher)
    }
}


fun signIn(
    activity: Activity,
    launch: (IntentSenderRequest) -> Unit
) {
    val signInClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder()
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