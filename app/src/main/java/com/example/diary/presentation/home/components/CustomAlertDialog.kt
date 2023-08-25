package com.example.diary.presentation.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.diary.R

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    isOpen: Boolean,
    onNoClick: () -> Unit,
    onYesClick: () -> Unit
) {

    if (isOpen) {
        AlertDialog(
            onDismissRequest = {
                onNoClick()
            },
            dismissButton = {
                OutlinedButton(onClick = onNoClick) {
                    Text(text = stringResource(R.string.no_option))
                }
            },
            confirmButton = {
                Button(onClick = onYesClick) {
                    Text(text = stringResource(R.string.yes_option))
                }
            },
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Normal
                )
            }
        )
    }
}