package com.example.diary.navigation

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diary.presentation.authentication.AuthScreen
import com.example.diary.presentation.authentication.AuthScreenViewModel
import com.example.diary.presentation.authentication.GoogleOneTap
import com.example.diary.presentation.authentication.signIn


@Composable
fun SetupNavGraph(
    startDestination: String,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authenticationRoute()
        homeRoute()
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.Authentication.route) {
        val authViewModel: AuthScreenViewModel = hiltViewModel()
        val state = authViewModel.state.collectAsState().value

        val activity = LocalContext.current as Activity

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
                    signIn(
                        activity = activity,
                        launch = { intentSenderRequest ->
                            launcher.launch(intentSenderRequest)
                        }
                    )
                }
            },
            onResultReceived = { token ->
                Log.d("TOKEN", "authenticationRoute: $token")
                authViewModel.signInWithMongoAtlas(
                    token = token,
                    onSuccess = { isSuccessfullyLoggedIn ->
                        if (isSuccessfullyLoggedIn) {
                            authViewModel.addErrorOrMessage(message = "Successfully Authenticated.")
                            authViewModel.onSignInClick(false)
                        }
                    },
                    onError = { exception ->
                        authViewModel.addErrorOrMessage(error = exception)
                    }
                )
            },
            onDismissDialog = { error ->
                authViewModel.addErrorOrMessage(error = Exception(error))
                authViewModel.onSignInClick(false)
            }
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {

    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(
            navArgument(name = "diaryId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { args ->
        val diaryId = args.arguments?.getString("diaryId")
    }
}