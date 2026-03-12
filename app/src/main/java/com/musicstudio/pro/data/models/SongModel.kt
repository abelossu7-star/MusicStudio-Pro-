package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val genre: String? = null,
    val mood: String? = null,
    val audioUrl: String,
    val coverImage: String? = null,
    val duration: Int? = null,
    val plays: Int? = null,
    val likes: Int? = null,
    val comments: Int? = null,
    val createdAt: String? = null
)
