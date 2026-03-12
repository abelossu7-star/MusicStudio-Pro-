package com.musicstudio.pro.features.studio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudioScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Studio")
        Button(onClick = { /* launch AI pipeline */ }, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Generate Song")
        }
        Button(onClick = { /* open beat generator */ }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Generate Beats")
        }
        Button(onClick = { /* record vocals */ }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Record Vocals")
        }
    }
}
