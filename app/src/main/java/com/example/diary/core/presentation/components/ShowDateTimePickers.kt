package com.example.diary.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDateTimePickers(
    showDateDialog: MutableState<Boolean>,
    showTimeDialog: MutableState<Boolean>,
    dateTimeState: DatePickerState,
    timePickerState: TimePickerState,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onDateReset:() -> Unit ={}
) {
    if (showDateDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                showDateDialog.value = false
            },
            confirmButton = {
                Button(onClick = {
                    dateTimeState.selectedDateMillis?.let { timestamp ->
                        onDateSelected(timestamp)
                    }
                    showDateDialog.value = false
                    showTimeDialog.value = true
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
                    state = dateTimeState
                )
            }
        )
    }

    if (showTimeDialog.value) {
        TimePickerDialog(
            onCancel = {
                showTimeDialog.value = false
            },
            onConfirm = {
                onTimeSelected(
                    timePickerState.hour,
                    timePickerState.minute
                )
                showTimeDialog.value = false
            },
            content = {
                TimePicker(state = timePickerState, layoutType = TimePickerLayoutType.Vertical)
            }
        )
    }
}
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}