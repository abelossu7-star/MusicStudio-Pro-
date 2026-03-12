package com.musicstudio.pro.features.upload

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicstudio.pro.features.upload.upload_song.UploadSongScreen
import com.musicstudio.pro.features.upload.upload_video.UploadVideoScreen

@Composable
fun UploadScreen() {
    var screenState by remember { mutableStateOf<UploadScreenState>(UploadScreenState.Menu) }

    when (screenState) {
        UploadScreenState.Menu -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "Upload")
                Button(onClick = { screenState = UploadScreenState.Song }, modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "Upload Song")
                }
                Button(onClick = { screenState = UploadScreenState.Video }, modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = "Upload Video")
                }
            }
        }
        UploadScreenState.Song -> {
            Column(modifier = Modifier.fillMaxSize()) {
                UploadSongScreen()
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { screenState = UploadScreenState.Menu }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Back")
                }
            }
        }
        UploadScreenState.Video -> {
            Column(modifier = Modifier.fillMaxSize()) {
                UploadVideoScreen()
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { screenState = UploadScreenState.Menu }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Back")
                }
            }
        }
    }
}

enum class UploadScreenState {
    Menu,
    Song,
    Video
}
