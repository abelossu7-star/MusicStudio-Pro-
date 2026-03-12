package com.musicstudio.pro.services.ai

import com.musicstudio.pro.core.SupabaseConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AIService {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val baseUrl = "${SupabaseConfig.url}/functions/v1"

    @JsonClass(generateAdapter = true)
    private data class PromptPayload(val prompt: String)

    @JsonClass(generateAdapter = true)
    private data class UrlResponse(
        val url: String? = null,
        val audioUrl: String? = null,
        val voiceUrl: String? = null
    )

    @JsonClass(generateAdapter = true)
    private data class LyricsResponse(val lyrics: String?)

    suspend fun generateLyrics(prompt: String): String = withContext(Dispatchers.IO) {
        callEdgeFunction("ai-generate-lyrics", PromptPayload(prompt))
            ?.let { response ->
                moshi.adapter(LyricsResponse::class.java).fromJson(response)?.lyrics
            }
            ?: "Generated lyrics for: $prompt"
    }

    suspend fun generateBeat(prompt: String): String = withContext(Dispatchers.IO) {
        callEdgeFunction("ai-generate-beat", PromptPayload(prompt))
            ?.let { response ->
                moshi.adapter(UrlResponse::class.java).fromJson(response)?.audioUrl
                    ?: moshi.adapter(UrlResponse::class.java).fromJson(response)?.url
            }
            ?: "https://example.com/generated_beat.mp3"
    }

    suspend fun cloneVoice(audioUrl: String): String = withContext(Dispatchers.IO) {
        // Edge function name is speculative; adjust as needed to match backend.
        callEdgeFunction("ai-clone-voice", mapOf("audioUrl" to audioUrl))
            ?.let { response ->
                moshi.adapter(UrlResponse::class.java).fromJson(response)?.voiceUrl
                    ?: moshi.adapter(UrlResponse::class.java).fromJson(response)?.url
            }
            ?: "https://example.com/cloned_voice.wav"
    }

    private fun callEdgeFunction(path: String, payload: Any): String? {
        val url = "$baseUrl/$path"

        val bodyJson = when (payload) {
            is PromptPayload -> moshi.adapter(PromptPayload::class.java).toJson(payload)
            is Map<*, *> -> {
                val mapAdapter = moshi.adapter<Map<String, Any?>>(Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    Any::class.java
                ))
                mapAdapter.toJson(payload.filterKeys { it is String } as Map<String, Any?>)
            }
            else -> moshi.adapter(Any::class.java).toJson(payload)
        }

        val requestBody = bodyJson.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("apikey", SupabaseConfig.anonKey)
            .addHeader("Authorization", "Bearer ${SupabaseConfig.anonKey}")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    null
                } else {
                    response.body?.string()
                }
            }
        } catch (t: Throwable) {
            null
        }
    }
}
