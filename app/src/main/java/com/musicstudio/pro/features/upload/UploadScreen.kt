package com.musicstudio.pro.features.upload

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UploadScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Upload")
        Button(onClick = { /* open upload song */ }, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Upload Song")
        }
        Button(onClick = { /* open upload video */ }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Upload Video")
        }
    }
}
