package com.musicstudio.pro.services.realtime

import android.content.Context
import com.musicstudio.pro.core.SupabaseConfig
import com.musicstudio.pro.data.models.Message
import com.musicstudio.pro.services.supabase.SupabaseService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class RealtimeService(private val context: Context, private val supabaseService: SupabaseService) {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()

    /**
     * Subscribes to a (polled) message feed and emits incoming messages.
     * The implementation uses polling via Supabase REST API to avoid introducing a
     * heavy realtime dependency in this sample.
     */
    fun subscribeToMessages(conversationId: String): Flow<Message> = callbackFlow {
        val scope = CoroutineScope(Dispatchers.IO + Job())
        val listType = Types.newParameterizedType(List::class.java, Message::class.java)
        val adapter = moshi.adapter<List<Message>>(listType)

        var lastMessageId: String? = null
        val pollIntervalMs = 3_000L

        val job = scope.launch {
            while (isActive) {
                try {
                    val messages = fetchNewMessages(conversationId, lastMessageId, adapter)
                    if (messages.isNotEmpty()) {
                        messages.forEach { trySend(it) }
                        lastMessageId = messages.last().id
                    }
                } catch (_: Throwable) {
                    // Ignore errors; caller can retry by re-subscribing.
                }
                delay(pollIntervalMs)
            }
        }

        awaitClose {
            job.cancel()
        }
    }

    private fun fetchNewMessages(
        conversationId: String,
        afterId: String?,
        adapter: com.squareup.moshi.JsonAdapter<List<Message>>
    ): List<Message> {
        val urlBuilder = "${SupabaseConfig.url}/rest/v1/messages".toHttpUrl().newBuilder()
            .addQueryParameter("receiver_id", "eq.$conversationId")
            .addQueryParameter("order", "created_at.asc")

        if (!afterId.isNullOrBlank()) {
            urlBuilder.addQueryParameter("id", "gt.$afterId")
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .addHeader("apikey", SupabaseConfig.anonKey)
            .addHeader("Authorization", "Bearer ${SupabaseConfig.anonKey}")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string().orEmpty()
            return adapter.fromJson(body) ?: emptyList()
        }
    }
}
