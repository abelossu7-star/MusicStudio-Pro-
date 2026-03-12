package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tip(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val amount: Double
)
