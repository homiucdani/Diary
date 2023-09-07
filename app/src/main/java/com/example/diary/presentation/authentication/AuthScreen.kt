package com.example.diary.presentation.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diary.R
import com.example.diary.core.presentation.components.MessageBarUi
import com.example.diary.core.presentation.util.MessageBarUi
import com.example.diary.presentation.authentication.components.GoogleButton

@Composable
fun AuthScreen(
    state: AuthScreenState,
    onGoogleButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { paddingValues ->
        AuthenticationContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onGoogleButton = onGoogleButton,
            isLoading = {
                state.isLoading
            },
            messageBarUi = {
                state.messageBarUi
            }
        )
    }
}


@Composable
private fun AuthenticationContent(
    modifier: Modifier = Modifier,
    isLoading: () -> Boolean,
    messageBarUi: () -> MessageBarUi,
    onGoogleButton: () -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MessageBarUi(
            modifier = Modifier
                .fillMaxWidth(),
            messageBarUi = messageBarUi()
        )

        Column(
            modifier = Modifier
                .weight(8f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = stringResource(
                    id = R.string.google_button
                ),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(R.string.please_sign_in_to_continue),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoogleButton(
                onClick = onGoogleButton,
                isLoading = isLoading()
            )
        }
    }
}