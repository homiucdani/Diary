package com.example.diary.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.auth.navigation.authenticationRoute
import com.example.details.navigation.detailsRoute
import com.example.home.navigation.homeRoute
import com.example.util.Screen

@Composable
fun SetupNavGraph(
    startDestination: String,
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authenticationRoute(
            navigateToHome = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
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
                navHostController.navigate(Screen.Details.passDiaryId(diaryId))
            },
            onDataLoaded = onDataLoaded
        )

        detailsRoute(
            onBackClick = {
                navHostController.popBackStack()
            },
            snackbarHostState = snackbarHostState,
            popOnDelete = {
                navHostController.popBackStack()
            }
        )
    }
}

