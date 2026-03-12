package com.musicstudio.pro.features.upload.upload_song

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.musicstudio.pro.data.models.UiState

@Composable
fun UploadSongScreen(viewModel: UploadSongViewModel = hiltViewModel()) {
    val state by viewModel.uploadState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadSong(it) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Upload Song")
        OutlinedTextField(
            value = viewModel.title.value,
            onValueChange = { viewModel.title.value = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.description.value,
            onValueChange = { viewModel.description.value = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.genre.value,
            onValueChange = { viewModel.genre.value = it },
            label = { Text("Genre") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.mood.value,
            onValueChange = { viewModel.mood.value = it },
            label = { Text("Mood") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(onClick = { launcher.launch("audio/*") }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text(text = "Pick audio file")
        }

        when (state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
            is UiState.Error -> Text(text = (state as UiState.Error).message, color = androidx.compose.ui.graphics.Color.Red)
            is UiState.Success -> Text(text = (state as UiState.Success<String>).data, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
