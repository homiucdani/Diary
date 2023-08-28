package com.example.diary.navigation

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diary.presentation.authentication.AuthScreen
import com.example.diary.presentation.authentication.AuthScreenViewModel
import com.example.diary.presentation.authentication.GoogleOneTap
import com.example.diary.presentation.home.HomeScreen
import com.example.diary.presentation.home.HomeViewModel
import kotlinx.coroutines.delay


@Composable
fun SetupNavGraph(
    startDestination: String,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authenticationRoute(
            navigateToHome = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            }
        )

        homeRoute(
            onAddNewDiary = {
                navHostController.navigate(Screen.Details.route)
            },
            navigateToAuth = {
                navHostController.navigate(Screen.Authentication.route) {
                    popUpTo(route = Screen.Home.route) {
                        inclusive = true
                    }
                }
            },
            navigateToDiaryScreen = { diaryId ->

            }
        )
        detailsRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(navigateToHome: () -> Unit) {
    composable(
        route = Screen.Authentication.route
    ) {
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
                    authViewModel.requestOneTap(
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
                    token = token
                )
            },
            onDismissDialog = { error ->
                authViewModel.addErrorOrMessage(error = Exception(error))
                authViewModel.onSignInClick(false)
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

fun NavGraphBuilder.homeRoute(
    onAddNewDiary: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToDiaryScreen: (String) -> Unit
) {
    composable(route = Screen.Home.route) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        val state = homeViewModel.state.collectAsState().value

        HomeScreen(
            state = state,
            onSignOut = {
                homeViewModel.signOut()
                navigateToAuth()
            },
            onAddNewDiary = onAddNewDiary,
            onDiaryClick = navigateToDiaryScreen
        )
    }
}

fun NavGraphBuilder.detailsRoute() {
    composable(
        route = Screen.Details.route,
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