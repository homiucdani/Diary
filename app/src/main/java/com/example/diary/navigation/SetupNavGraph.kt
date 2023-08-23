package com.example.diary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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