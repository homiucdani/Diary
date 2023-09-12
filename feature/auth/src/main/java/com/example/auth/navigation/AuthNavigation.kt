package com.example.auth.navigation

import android.app.Activity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.auth.AuthScreen
import com.example.auth.AuthScreenViewModel
import com.example.auth.GoogleOneTap
import com.example.util.Screen
import kotlinx.coroutines.delay

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(
        route = Screen.Authentication.route
    ) {
        val authViewModel: AuthScreenViewModel = hiltViewModel()
        val state = authViewModel.state.collectAsState().value

        val activity = LocalContext.current as Activity

        LaunchedEffect(key1 = true) {
            onDataLoaded()
        }

        AuthScreen(
            onGoogleButton = {
                authViewModel.onSignInClick(true)
            },
            state = state
        )

        GoogleOneTap(
            key = state.isLoading,
            launch = { launcher ->
                if (state.isLoading) {
                    authViewModel.requestOneTap(
                        activity = activity,
                        launch = { intentSenderRequest ->
                            launcher.launch(intentSenderRequest)
                        }
                    )
                }
            },
            onResultReceived = { token ->
                authViewModel.signInWithFirebase(token)
            },
            onDismissDialog = { error ->
                authViewModel.addErrorOrMessage(error = Exception(error))
            }
        )

        LaunchedEffect(key1 = state.isAuthenticated) {
            if (state.isAuthenticated) {
                delay(600)
                navigateToHome()
            }
        }
    }
}