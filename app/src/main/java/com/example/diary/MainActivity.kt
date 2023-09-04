package com.example.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.diary.navigation.Screen
import com.example.diary.navigation.SetupNavGraph
import com.example.diary.ui.theme.DiaryTheme
import com.example.diary.util.Constants.APP_ID
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val app = App.create(APP_ID)

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreen
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryTheme {
                val navController = rememberNavController()
                val snackbarHostState = SnackbarHostState()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navHostController = navController,
                    snackbarHostState = snackbarHostState,
                    onDataLoaded = {
                        keepSplashScreen = false
                    }
                )
            }
        }
    }

    private fun getStartDestination(): String {
        val user = app.currentUser
        return if (user != null && user.loggedIn) {
            Screen.Home.route
        } else {
            Screen.Authentication.route
        }
    }
}
