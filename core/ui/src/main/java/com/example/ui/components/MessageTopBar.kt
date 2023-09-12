package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.util.MessageBarUi
import com.example.ui.R
import com.example.ui.theme.InfoGreen
import kotlinx.coroutines.delay
import java.net.ConnectException
import java.net.SocketTimeoutException

@Composable
fun MessageTopBar(
    modifier: Modifier = Modifier,
    messageBarUi: MessageBarUi
) {
    var errorMessage by remember {
        mutableStateOf("")
    }

    var showMessage by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = messageBarUi) {
        showMessage = true
        if (messageBarUi.exception != null) {
            errorMessage = when (messageBarUi.exception) {
                is SocketTimeoutException -> {
                    "Connection Timeout"
                }

                is ConnectException -> {
                    "Internet Connection Unavailable"
                }

                else -> {
                    "${messageBarUi.exception.message}"
                }
            }
        }
        delay(3000)
        showMessage = false
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = messageBarUi.message != null && showMessage ||
                messageBarUi.exception != null && showMessage,
        enter = expandVertically(
            animationSpec = tween(durationMillis = 300),
            expandFrom = Alignment.Top
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = if (messageBarUi.exception != null) MaterialTheme.colorScheme.onErrorContainer else InfoGreen)
                .padding(10.dp)
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (messageBarUi.exception != null) Icons.Default.Warning else Icons.Default.Check,
                contentDescription = stringResource(R.string.warning_icon),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = if (messageBarUi.exception != null) errorMessage else messageBarUi.message.toString(),
                color = if (messageBarUi.exception != null) MaterialTheme.colorScheme.onError else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}