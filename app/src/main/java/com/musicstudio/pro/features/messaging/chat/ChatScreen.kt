package com.musicstudio.pro.features.messaging.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.musicstudio.pro.data.models.Message
import com.musicstudio.pro.data.models.UiState

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    var receiverId by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Chat")

        OutlinedTextField(
            value = receiverId,
            onValueChange = { receiverId = it },
            label = { Text("Receiver ID") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(onClick = { viewModel.loadConversation(receiverId) }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Connect")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            when (uiState) {
                is UiState.Loading -> Text(text = "Loading messages...", modifier = Modifier.padding(8.dp))
                is UiState.Error -> Text(text = (uiState as UiState.Error).message, color = Color.Red)
                else -> Unit
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messages) { message ->
                    MessageRow(message)
                }
            }
        }

        val messageText = viewModel.messageText
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                label = { Text("Message") },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Button(onClick = { viewModel.sendMessage(receiverId) }) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
private fun MessageRow(message: Message) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp), elevation = 2.dp) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "From: ${message.senderId}")
            Text(text = message.messageText.orEmpty(), modifier = Modifier.padding(top = 4.dp))
        }
    }
}
