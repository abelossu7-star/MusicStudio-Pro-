package com.musicstudio.pro.features.home.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.musicstudio.pro.data.models.Song
import com.musicstudio.pro.data.models.UiState

@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val state by viewModel.feedState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    when (state) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Text(text = (state as UiState.Error).message, modifier = Modifier.padding(16.dp))
        }
        is UiState.Success -> {
            val songs = (state as UiState.Success<List<Song>>).data
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                items(songs) { song ->
                    SongCard(song)
                }
            }
        }
    }
}

@Composable
private fun SongCard(song: Song) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = song.title, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = "by ${song.userId}", modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Plays: ${song.plays} • Likes: ${song.likes}")
        }
    }
}
