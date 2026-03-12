package com.musicstudio.pro.features.profile.edit_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
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
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Edit Profile")
        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.bio.value,
            onValueChange = { viewModel.bio.value = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.profileImageUrl.value,
            onValueChange = { viewModel.profileImageUrl.value = it },
            label = { Text("Profile Image URL") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(onClick = { viewModel.saveProfile(); onDone() }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text(text = "Save")
        }

        when (state) {
            is UiState.Loading -> Text(text = "Loading...", modifier = Modifier.padding(top = 8.dp))
            is UiState.Error -> Text(text = (state as UiState.Error).message, modifier = Modifier.padding(top = 8.dp))
            is UiState.Success -> Text(text = "Saved!", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
