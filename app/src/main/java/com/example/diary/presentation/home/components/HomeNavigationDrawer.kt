package com.example.diary.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diary.R
import com.example.diary.core.presentation.components.CustomAlertDialog

@Composable
fun HomeNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: () -> DrawerState,
    onSignOut: () -> Unit,
    onDeleteAllClick: () -> Unit,
    content: @Composable () -> Unit
) {

    var signOutDialog by remember {
        mutableStateOf(false)
    }

    var deleteDialog by remember {
        mutableStateOf(false)
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState(),
        drawerContent = {
            ModalDrawerSheet {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(
                        R.string.logo
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(250.dp)
                        .align(CenterHorizontally)
                )
                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = stringResource(
                                    R.string.google_logo
                                ),
                                tint = Color.Unspecified
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Sign out",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }
                    },
                    selected = false,
                    onClick = {
                        signOutDialog = true
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(
                                    R.string.delete_all_diaries_icon
                                ),
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = stringResource(id = R.string.delete_all_diaries),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }
                    },
                    selected = false,
                    onClick = {
                        deleteDialog = true
                    }
                )
            }

        },
        content = content
    )

    CustomAlertDialog(
        title = stringResource(R.string.sign_out),
        message = "Are you sure you want to Sign Out from your Google Account?",
        isOpen = signOutDialog,
        onNoClick = {
            signOutDialog = false
        },
        onYesClick = {
            onSignOut()
            signOutDialog = false
        }
    )

    CustomAlertDialog(
        title = "Delete diaries",
        message = "Are you sure you want to delete all diaries?",
        isOpen = deleteDialog,
        onNoClick = {
            deleteDialog = false
        },
        onYesClick = {
            onDeleteAllClick()
            deleteDialog = false
        }
    )
}