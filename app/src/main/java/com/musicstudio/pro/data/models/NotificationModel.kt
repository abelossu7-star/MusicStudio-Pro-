package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationItem(
    val id: String,
    val userId: String,
    val type: String,
    val referenceId: String?,
    val isRead: Boolean = false
)
