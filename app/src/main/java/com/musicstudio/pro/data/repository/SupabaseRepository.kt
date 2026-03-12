package com.musicstudio.pro.data.repository

import com.musicstudio.pro.core.AppLog
import com.musicstudio.pro.data.models.*
import com.musicstudio.pro.services.supabase.SupabaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseRepository(private val service: SupabaseService) {

    fun login(email: String, password: String): Flow<Result<User>> = flow {
        runCatching {
            val response = service.auth.signInWithPassword(email, password)
            val apiUser = response.user ?: throw IllegalStateException("Login failed")
            User(
                id = apiUser.id,
                username = apiUser.email ?: "",
                email = apiUser.email ?: "",
                profileImage = null
            )
        }.onSuccess { emit(Result.success(it)) }
            .onFailure { emit(Result.failure(it)) }
    }

    fun register(email: String, password: String): Flow<Result<User>> = flow {
        runCatching {
            val response = service.auth.signUp(email, password)
            val apiUser = response.user ?: throw IllegalStateException("Registration failed")
            User(
                id = apiUser.id,
                username = apiUser.email ?: "",
                email = apiUser.email ?: "",
                profileImage = null
            )
        }.onSuccess { emit(Result.success(it)) }
            .onFailure { emit(Result.failure(it)) }
    }

    fun fetchFeed(): Flow<Result<List<Song>>> = flow {
        try {
            val response = service.database.from("songs").select("*").execute()
            val data = response.decode<List<Song>>()
            emit(Result.success(data))
        } catch (e: Exception) {
            AppLog.e("fetchFeed", e)
            emit(Result.failure(e))
        }
    }

    fun getSong(songId: String): Flow<Result<Song>> = flow {
        try {
            val response = service.database.from("songs").select("*").eq("id", songId).single().execute()
            val song = response.decode<Song>()
            emit(Result.success(song))
        } catch (e: Exception) {
            AppLog.e("getSong", e)
            emit(Result.failure(e))
        }
    }

    fun likeSong(songId: String, userId: String): Flow<Result<Unit>> = flow {
        try {
            service.database.from("likes").insert(Like(id = "", userId = userId, postId = songId)).execute()
            service.database.from("songs").update(mapOf("likes" to "likes + 1")).eq("id", songId).execute()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            AppLog.e("likeSong", e)
            emit(Result.failure(e))
        }
    }

    fun commentOnSong(songId: String, userId: String, commentText: String): Flow<Result<Comment>> = flow {
        try {
            val comment = Comment(id = "", userId = userId, postId = songId, comment = commentText)
            val response = service.database.from("comments").insert(comment).execute()
            val inserted = response.decode<List<Comment>>().firstOrNull() ?: throw IllegalStateException("Comment insert failed")
            emit(Result.success(inserted))
        } catch (e: Exception) {
            AppLog.e("commentOnSong", e)
            emit(Result.failure(e))
        }
    }

    fun followUser(followerId: String, followingId: String): Flow<Result<Unit>> = flow {
        try {
            service.database.from("followers").insert(Follower(id = "", followerId = followerId, followingId = followingId)).execute()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            AppLog.e("followUser", e)
            emit(Result.failure(e))
        }
    }

    fun sendTip(senderId: String, receiverId: String, amount: Double): Flow<Result<Tip>> = flow {
        try {
            val tip = Tip(id = "", senderId = senderId, receiverId = receiverId, amount = amount)
            val response = service.database.from("tips").insert(tip).execute()
            val inserted = response.decode<List<Tip>>().firstOrNull() ?: throw IllegalStateException("Tip failed")
            emit(Result.success(inserted))
        } catch (e: Exception) {
            AppLog.e("sendTip", e)
            emit(Result.failure(e))
        }
    }

    fun fetchMessages(conversationId: String): Flow<Result<List<Message>>> = flow {
        try {
            val response = service.database.from("messages").select("*").eq("id", conversationId).execute()
            val messages = response.decode<List<Message>>()
            emit(Result.success(messages))
        } catch (e: Exception) {
            AppLog.e("fetchMessages", e)
            emit(Result.failure(e))
        }
    }

    fun sendMessage(message: Message): Flow<Result<Message>> = flow {
        try {
            val response = service.database.from("messages").insert(message).execute()
            val inserted = response.decode<List<Message>>().firstOrNull() ?: throw IllegalStateException("Message send failed")
            emit(Result.success(inserted))
        } catch (e: Exception) {
            AppLog.e("sendMessage", e)
            emit(Result.failure(e))
        }
    }

    fun fetchNotifications(userId: String): Flow<Result<List<NotificationItem>>> = flow {
        try {
            val response = service.database.from("notifications").select("*").eq("user_id", userId).execute()
            val notifications = response.decode<List<NotificationItem>>()
            emit(Result.success(notifications))
        } catch (e: Exception) {
            AppLog.e("fetchNotifications", e)
            emit(Result.failure(e))
        }
    }

    fun uploadFile(bucket: String, path: String, bytes: ByteArray): Flow<Result<String>> = flow {
        try {
            val response = service.storage.from(bucket).upload(path, bytes)
            if (response.error == null) {
                val url = service.storage.from(bucket).createSignedUrl(path, 60 * 60 * 24)
                emit(Result.success(url.data ?: ""))
            } else {
                emit(Result.failure(Exception(response.error.message)))
            }
        } catch (e: Exception) {
            AppLog.e("uploadFile", e)
            emit(Result.failure(e))
        }
    }

    fun createSong(
        userId: String,
        title: String,
        description: String?,
        audioUrl: String,
        genre: String? = null,
        mood: String? = null,
        coverImage: String? = null
    ): Flow<Result<Song>> = flow {
        try {
            val payload = mapOf(
                "user_id" to userId,
                "title" to title,
                "description" to description,
                "audio_url" to audioUrl,
                "genre" to genre,
                "mood" to mood,
                "cover_image" to coverImage
            ).filterValues { it != null }

            val response = service.database.from("songs").insert(payload).single().execute()
            val song = response.decode<Song>()
            emit(Result.success(song))
        } catch (e: Exception) {
            AppLog.e("createSong", e)
            emit(Result.failure(e))
        }
    }

    fun currentUserId(): String? = service.auth.session?.user?.id

    fun fetchUser(userId: String): Flow<Result<User>> = flow {
        try {
            val response = service.database.from("users").select("*").eq("id", userId).single().execute()
            val user = response.decode<User>()
            emit(Result.success(user))
        } catch (e: Exception) {
            AppLog.e("fetchUser", e)
            emit(Result.failure(e))
        }
    }
}
