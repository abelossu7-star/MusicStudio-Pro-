package com.musicstudio.pro.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val id: String,
    @Json(name = "sender_id")
    val senderId: String,
    @Json(name = "receiver_id")
    val receiverId: String,
    @Json(name = "message_text")
    val messageText: String? = null,
    @Json(name = "image_url")
    val imageUrl: String? = null,
    @Json(name = "read_status")
    val readStatus: Boolean = false
)
