package com.example.diary.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diary.R
import com.example.diary.presentation.home.components.CustomAlertDialog
import com.example.diary.presentation.home.components.HomeContent
import com.example.diary.presentation.home.components.HomeNavigationDrawer
import com.example.diary.presentation.home.components.HomeTopAppBar
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onAddNewDiary: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var alertDialogState by remember {
        mutableStateOf(false)
    }

    HomeNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerState,
        onSignOut = {
            alertDialogState = true
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onMenuClicked = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onCalendarClick = {
                        // TODO() open the calendar dialog only
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddNewDiary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.add_new_diary)
                    )
                }
            },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) { paddingValues ->
            CustomAlertDialog(
                title = stringResource(R.string.sign_out),
                message = "Are you sure you want to Sign Out from your Google Account?",
                isOpen = alertDialogState,
                onNoClick = {
                    alertDialogState = false
                },
                onYesClick = onSignOut
            )

            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            )
        }
    }

}