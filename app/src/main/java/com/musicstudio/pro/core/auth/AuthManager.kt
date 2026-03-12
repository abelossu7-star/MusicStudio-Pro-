package com.musicstudio.pro.core.auth

import com.musicstudio.pro.data.models.User
import com.musicstudio.pro.services.supabase.SupabaseService

/**
 * Simple facade for authentication operations.
 * This is a convenience wrapper around Supabase auth.
 */
class AuthManager(private val supabaseService: SupabaseService) {
    suspend fun currentUser(): User? {
        val user = supabaseService.auth.session?.user
        return user?.let {
            User(
                id = it.id,
                username = it.email ?: "",
                email = it.email,
                profileImage = null
            )
        }
    }
}
