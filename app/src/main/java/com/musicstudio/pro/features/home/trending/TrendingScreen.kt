package com.musicstudio.pro.features.home.trending

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrendingScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Trending")
        Text(text = "Trending beats, songs, and creators will appear here.", modifier = Modifier.padding(top = 8.dp))
    }
}
