package com.example.diary.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.diary.presentation.details.DetailsScreen
import com.example.diary.presentation.details.DetailsViewModel
import com.example.diary.presentation.home.HomeScreen
import com.example.diary.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

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

fun NavGraphBuilder.homeRoute(
    onAddNewDiary: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToDiaryScreen: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val homeViewModel: HomeViewModel = viewModel()
        val state = homeViewModel.state.collectAsState().value

        LaunchedEffect(key1 = !state.isLoading) {
            onDataLoaded()
        }

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

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.detailsRoute(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    popOnDelete: () -> Unit
) {
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
        val detailsViewModel: DetailsViewModel = hiltViewModel()
        val state = detailsViewModel.state.collectAsState().value
        val galleryImageState = detailsViewModel.galleryImages

        val pagerState = rememberPagerState()
        val diaryId = args.arguments?.getString("diaryId")

        val context = LocalContext.current

        LaunchedEffect(key1 = detailsViewModel.uiEvent) {
            detailsViewModel.uiEvent.collectLatest { message ->
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }

        DetailsScreen(
            state = state,
            onBackClick = onBackClick,
            pagerState = {
                pagerState
            },
            onTitleChange = { title ->
                detailsViewModel.onTitleChange(title)
            },
            onDescriptionChange = { description ->
                detailsViewModel.onDescriptionChange(description)
            },
            onMoodChange = { mood ->
                detailsViewModel.onMoodChange(mood)
            },
            selectedDiaryId = diaryId,
            onSaveOrUpdateClick = {
                if (diaryId == null) {
                    detailsViewModel.insertDiaryIntoDb()
                    if (!state.isUploadingImages) {
                        onBackClick()
                    }
                } else {
                    detailsViewModel.updateDiaryIntoDb()
                    if (!state.isUploadingImages) {
                        onBackClick()
                    }
                }
            },
            snackbarHostState = { snackbarHostState },
            onDateTimeUpdate = { zonedDateTime ->
                detailsViewModel.updateDateTime(zonedDateTime)
            },
            onDeleteClick = {
                detailsViewModel.deleteDiary().also { popOnDelete() }
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            },
            galleryImages = {
                galleryImageState
            },
            onImageSelect = { imageUri ->
                //image '/' jpeg
                val type = context.contentResolver.getType(imageUri)?.split("/")?.last() ?: "jpg"
                detailsViewModel.addImage(
                    image = imageUri,
                    imageType = type
                )
            },
            onDeleteImageClick = { galleryImage ->
                detailsViewModel.scheduleRemove(galleryImage)
            }
        )
    }
}