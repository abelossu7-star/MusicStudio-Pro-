package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Playlist(
    val id: String,
    val userId: String,
    val title: String
)
