package com.example.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.components.CustomAlertDialog
import com.example.ui.components.ShowDateTimePickers
import com.example.util.formatDateTimeToPattern
import com.example.util.toInstant
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import com.example.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopAppBar(
    selectedDiaryId: String?,
    diaryTitle: () -> String,
    diaryTime: () -> Instant,
    moodName: () -> String,
    onBackClick: () -> Unit,
    onDateTimeUpdate: (ZonedDateTime) -> Unit,
    onDeleteClick: () -> Unit
) {

    val showDateDialog = remember {
        mutableStateOf(false)
    }

    val showTimeDialog = remember {
        mutableStateOf(false)
    }

    val currentDate = remember {
        mutableStateOf(LocalDate.now())
    }

    val currentTime = remember {
        mutableStateOf(LocalTime.now())
    }

    val dateState = rememberDatePickerState()
    val timeState = rememberTimePickerState()


    CenterAlignedTopAppBar(
        title = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = moodName(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                )

                Text(
                    text = diaryTime().formatDateTimeToPattern("dd MMM yyyy, hh:mm a").uppercase(),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_arrow),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            DateAction(
                onDateClick = {
                    showDateDialog.value = true
                }
            )
            if (selectedDiaryId != null) {
                MoreSettingsAction(
                    diaryTitle = diaryTitle(),
                    onDeleteClick = onDeleteClick
                )
            }
        }
    )

    ShowDateTimePickers(
        showDateDialog = showDateDialog,
        showTimeDialog = showTimeDialog,
        dateTimeState = dateState,
        timePickerState = timeState,
        onDateSelected = { timestamp ->
            currentDate.value =
                LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()).toLocalDate()
        },
        onTimeSelected = { hour, minute ->
            currentTime.value = LocalTime.of(hour, minute)
            if (currentDate.value != null && currentTime.value != null) {
                onDateTimeUpdate(
                    ZonedDateTime.of(currentDate.value, currentTime.value, ZoneId.systemDefault())
                )
            }
        }
    )
}

@Composable
fun DateAction(
    onDateClick: () -> Unit
) {
    IconButton(
        onClick = onDateClick
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.date_icon),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MoreSettingsAction(
    diaryTitle: String,
    onDeleteClick: () -> Unit
) {

    var showDropDown by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            showDropDown = true
        }
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more_settings),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    DropdownMenu(
        expanded = showDropDown,
        onDismissRequest = {
            showDropDown = false
        }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            },
            onClick = {
                showDialog = true
            }
        )
    }
    CustomAlertDialog(
        title = "Delete: $diaryTitle",
        message = "Are you sure you want to delete: '$diaryTitle', this action cannot be reversed.",
        isOpen = showDialog,
        onNoClick = {
            showDialog = false
        },
        onYesClick = {
            showDialog = false
            showDropDown = false
            onDeleteClick()
        }
    )
}