package com.example.diary.presentation.home

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diary.R
import com.example.diary.presentation.home.components.HomeContent
import com.example.diary.presentation.home.components.HomeNavigationDrawer
import com.example.diary.presentation.home.components.HomeTopAppBar
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
fun HomeScreen(
    state: HomeState,
    onSignOut: () -> Unit,
    onAddNewDiary: () -> Unit,
    onDiaryClick: (String) -> Unit,
    onDeleteAllClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDateReset: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    HomeNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = {
            drawerState
        },
        onSignOut = onSignOut,
        onDeleteAllClick = onDeleteAllClick
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onMenuClicked = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onDateSelected = onDateSelected,
                    onDateReset = onDateReset
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
                .background(color = MaterialTheme.colorScheme.surface)
        ) { paddingValues ->
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .padding(20.dp),
                onDiaryClick = onDiaryClick,
                allDiaries = {
                    state.diaries
                },
                isLoading = {
                    state.isLoading
                }
            )
        }
    }
}