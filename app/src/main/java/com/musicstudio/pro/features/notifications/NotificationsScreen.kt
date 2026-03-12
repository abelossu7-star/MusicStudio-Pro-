package com.musicstudio.pro.features.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.musicstudio.pro.data.models.UiState

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = hiltViewModel()) {
    val state by viewModel.notifications.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Notifications")

        when (state) {
            is UiState.Loading -> Text(text = "Loading...", modifier = Modifier.padding(top = 8.dp))
            is UiState.Error -> Text(text = (state as UiState.Error).message, modifier = Modifier.padding(top = 8.dp))
            is UiState.Success -> {
                val items = (state as UiState.Success).data
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(items) { notification ->
                        Text(text = "${notification.type} - ${notification.referenceId}", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
