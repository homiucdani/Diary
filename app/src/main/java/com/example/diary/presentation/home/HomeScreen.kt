package com.example.diary.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diary.R
import com.example.diary.core.presentation.components.CustomAlertDialog
import com.example.diary.presentation.home.components.HomeContent
import com.example.diary.presentation.home.components.HomeNavigationDrawer
import com.example.diary.presentation.home.components.HomeTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    state: HomeState,
    onSignOut: () -> Unit,
    onAddNewDiary: () -> Unit,
    onDiaryClick: (String) -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var alertDialogState by remember {
        mutableStateOf(false)
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                    },
                    scrollBehavior = scrollBehavior
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
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .background(color = MaterialTheme.colorScheme.surface)
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
                    .padding(horizontal = 24.dp),
                onDiaryClick = onDiaryClick,
                allDiaries = state.diaries,
                isLoading = state.isLoading
            )
        }
    }
}