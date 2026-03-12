package com.musicstudio.pro.features.tipping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TippingScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Tip a Creator")
        Button(onClick = { /* send tip */ }, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Send Tip")
        }
    }
}
