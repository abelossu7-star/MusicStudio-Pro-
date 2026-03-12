package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Follower(
    val id: String,
    val followerId: String,
    val followingId: String
)
