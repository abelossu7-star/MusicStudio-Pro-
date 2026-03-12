package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(
    val id: String,
    val userId: String,
    val postId: String,
    val comment: String
)
