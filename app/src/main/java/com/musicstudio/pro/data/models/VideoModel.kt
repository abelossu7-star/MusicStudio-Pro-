package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(
    val id: String,
    val userId: String,
    val videoUrl: String,
    val caption: String? = null,
    val likes: Int = 0,
    val views: Int = 0
)
