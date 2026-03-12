package com.musicstudio.pro.services.elevenlabs

import com.musicstudio.pro.core.ElevenLabsConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ElevenLabsService {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val baseUrl = "https://api.elevenlabs.io/v1"

    @JsonClass(generateAdapter = true)
    private data class TtsRequest(
        val text: String,
        val voice_settings: VoiceSettings? = null
    )

    @JsonClass(generateAdapter = true)
    private data class VoiceSettings(
        val stability: Double? = null,
        val similarity_boost: Double? = null
    )

    suspend fun synthesizeSpeech(
        voiceId: String,
        text: String,
        stability: Double? = null,
        similarityBoost: Double? = null
    ): ByteArray? = withContext(Dispatchers.IO) {
        val requestBody = moshi.adapter(TtsRequest::class.java).toJson(
            TtsRequest(
                text = text,
                voice_settings = VoiceSettings(
                    stability = stability,
                    similarity_boost = similarityBoost
                )
            )
        ).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("$baseUrl/text-to-speech/$voiceId")
            .post(requestBody)
            .addHeader("xi-api-key", ElevenLabsConfig.apiKey)
            .addHeader("Accept", "audio/mpeg")
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    null
                } else {
                    response.body?.bytes()
                }
            }
        } catch (t: Throwable) {
            null
        }
    }
}
