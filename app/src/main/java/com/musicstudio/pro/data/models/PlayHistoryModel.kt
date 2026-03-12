package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayHistory(
    val id: String,
    val userId: String,
    val songId: String
)
