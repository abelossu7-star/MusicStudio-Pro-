package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String? = null,
    val email: String? = null,
    val profileImage: String? = null,
    val bio: String? = null,
    val verified: Boolean? = false,
    val followersCount: Int? = null,
    val followingCount: Int? = null,
    val totalTipsReceived: Double? = null,
    val createdAt: String? = null
)
