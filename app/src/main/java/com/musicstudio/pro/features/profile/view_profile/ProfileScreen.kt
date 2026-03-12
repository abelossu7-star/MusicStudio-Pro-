package com.musicstudio.pro.features.profile.view_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Profile")
        Button(onClick = { /* navigate to edit */ }, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Edit Profile")
        }
    }
}
