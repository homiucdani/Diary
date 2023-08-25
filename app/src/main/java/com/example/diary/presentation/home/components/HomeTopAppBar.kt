package com.example.diary.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.diary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onMenuClicked: () -> Unit,
    onCalendarClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.diary))
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onMenuClicked()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_icon)
                )
            }
        },
        actions = {
            CalendarActionButton(
                onCalendarClick = onCalendarClick
            )
        }
    )
}

@Composable
fun CalendarActionButton(
    onCalendarClick: () -> Unit
) {
    IconButton(
        onClick = {
            onCalendarClick()
        }
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.calendar_icon),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}