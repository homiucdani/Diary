package com.example.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.util.toInstant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import com.example.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onMenuClicked: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDateReset: () -> Unit
) {

    val showDateDialog = remember {
        mutableStateOf(false)
    }

    val dateState = rememberDatePickerState()

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
                onCalendarClick = {
                    showDateDialog.value = true
                }
            )
        }
    )

    if (showDateDialog.value){
        DatePickerDialog(
            onDismissRequest = {
                showDateDialog.value = false
            },
            confirmButton = {
                Button(onClick = {
                    dateState.selectedDateMillis?.let { timestamp ->
                        val date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                    showDateDialog.value = false
                }) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDateDialog.value = false
                    onDateReset()
                }) {
                    Text(text = "Cancel")
                }
            },
            content = {
                DatePicker(
                    state = dateState
                )
            }
        )
    }
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