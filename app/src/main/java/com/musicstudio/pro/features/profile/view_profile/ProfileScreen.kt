package com.musicstudio.pro.features.profile.view_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicstudio.pro.features.notifications.NotificationsScreen
import com.musicstudio.pro.features.profile.edit_profile.EditProfileScreen
import com.musicstudio.pro.features.tipping.TippingScreen

@Composable
fun ProfileScreen() {
    var screen by remember { mutableStateOf<ProfileScreenState>(ProfileScreenState.Home) }

    when (screen) {
        ProfileScreenState.Home -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "Profile")
                Button(onClick = { screen = ProfileScreenState.Edit }, modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "Edit Profile")
                }
                Button(onClick = { screen = ProfileScreenState.Notifications }, modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "Notifications")
                }
                Button(onClick = { screen = ProfileScreenState.Tipping }, modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "Tip a Creator")
                }
            }
        }
        ProfileScreenState.Edit -> {
            EditProfileScreen(onDone = { screen = ProfileScreenState.Home })
        }
        ProfileScreenState.Notifications -> {
            Column(modifier = Modifier.fillMaxSize()) {
                NotificationsScreen()
                Button(onClick = { screen = ProfileScreenState.Home }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Back")
                }
            }
        }
        ProfileScreenState.Tipping -> {
            Column(modifier = Modifier.fillMaxSize()) {
                TippingScreen()
                Button(onClick = { screen = ProfileScreenState.Home }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Back")
                }
            }
        }
    }
}

private enum class ProfileScreenState {
    Home,
    Edit,
    Notifications,
    Tipping
}
