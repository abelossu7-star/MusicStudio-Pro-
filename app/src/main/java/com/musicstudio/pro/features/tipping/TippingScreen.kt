package com.musicstudio.pro.features.tipping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.musicstudio.pro.data.models.UiState

@Composable
fun TippingScreen(viewModel: TippingViewModel = hiltViewModel()) {
    val state by viewModel.tipState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Tip a Creator")

        OutlinedTextField(
            value = viewModel.receiverId.value,
            onValueChange = { viewModel.receiverId.value = it },
            label = { Text("Creator ID") },
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = viewModel.amount.value.toString(),
            onValueChange = { viewModel.amount.value = it.toDoubleOrNull() ?: 0.0 },
            label = { Text("Amount (USD)") },
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
        )

        Button(onClick = { viewModel.sendTip() }, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Send Tip")
        }

        when (state) {
            is UiState.Loading -> Text(text = "Sending tip...", modifier = Modifier.padding(top = 8.dp))
            is UiState.Error -> Text(text = (state as UiState.Error).message, modifier = Modifier.padding(top = 8.dp))
            is UiState.Success -> Text(text = "Tip sent!", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
