package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AiGeneratedSong(
    val id: String,
    val userId: String,
    val prompt: String,
    val genre: String?,
    val lyrics: String? = null,
    val audioUrl: String
)
