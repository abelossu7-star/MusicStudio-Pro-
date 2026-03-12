package com.musicstudio.pro.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoiceSample(
    val id: String,
    val userId: String,
    val voiceUrl: String
)
