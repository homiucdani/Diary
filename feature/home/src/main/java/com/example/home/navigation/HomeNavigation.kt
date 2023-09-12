package com.example.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.home.HomeScreen
import com.example.home.HomeViewModel
import com.example.util.Screen

fun NavGraphBuilder.homeRoute(
    onAddNewDiary: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToDiaryScreen: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val homeViewModel: HomeViewModel = hiltViewModel()
        val state = homeViewModel.state.collectAsState().value

        LaunchedEffect(key1 = !state.isLoading) {
            onDataLoaded()
        }

        HomeScreen(
            state = state,
            onSignOut = {
                homeViewModel.signOut().also { navigateToAuth() }
            },
            onAddNewDiary = onAddNewDiary,
            onDiaryClick = navigateToDiaryScreen,
            onDeleteAllClick = {
                homeViewModel.deleteAllDiaries(
                    onSuccess = {
                        android.widget.Toast.makeText(context, "All diaries deleted.", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    onError = { exception ->
                        android.widget.Toast.makeText(context, "${exception.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onDateSelected = { localDate ->
                homeViewModel.observeFilteredOrNonFilterDiaries(localDate)
            },
            onDateReset = {
                homeViewModel.observeAllDiaries()
            }
        )
    }
}