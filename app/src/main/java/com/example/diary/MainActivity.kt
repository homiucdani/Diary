package com.example.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.diary.data.local.ImageToDeleteDao
import com.example.diary.data.local.ImageToUploadDao
import com.example.diary.navigation.Screen
import com.example.diary.navigation.SetupNavGraph
import com.example.diary.ui.theme.DiaryTheme
import com.example.diary.util.Constants.APP_ID
import com.example.diary.util.retryDeleteImages
import com.example.diary.util.retryUploadImages
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val app = App.create(APP_ID)

    private var keepSplashScreen = true

    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreen
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
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
        cleanupImageTasks(
            scope = lifecycleScope,
            imageToUploadDao = imageToUploadDao,
            imageToDeleteDao = imageToDeleteDao
        )
    }

    private fun getStartDestination(): String {
        val user = app.currentUser
        return if (user != null && user.loggedIn) {
            Screen.Home.route
        } else {
            Screen.Authentication.route
        }
    }

    private fun cleanupImageTasks(
        scope: CoroutineScope,
        imageToUploadDao: ImageToUploadDao,
        imageToDeleteDao: ImageToDeleteDao
    ) {
        scope.launch(Dispatchers.IO) {
            val result = imageToUploadDao.getAllImages()
            result.forEach { imageToUpload ->
                retryUploadImages(
                    imageToUpload = imageToUpload,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imageToUploadDao.cleanupImage(imageToUpload.id)
                        }
                    }
                )
            }
            cleanupImageToDeleteCheck(
                scope = scope,
                imageToDeleteDao = imageToDeleteDao
            )
        }
    }

    private fun cleanupImageToDeleteCheck(
        scope: CoroutineScope,
        imageToDeleteDao: ImageToDeleteDao
    ) {
        scope.launch(Dispatchers.IO) {
            imageToDeleteDao.getAllImagesToDelete().collect { images ->
                images.forEach { imageToDelete ->
                    retryDeleteImages(
                        imageToDelete = imageToDelete,
                        onSuccess = {
                            scope.launch {
                                imageToDeleteDao.cleanupImages(imageToDelete.id)
                            }
                        }
                    )
                }
            }
        }
    }
}
