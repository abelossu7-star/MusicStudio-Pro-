package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistSong(
    val id: String,
    val playlistId: String,
    val songId: String
)
