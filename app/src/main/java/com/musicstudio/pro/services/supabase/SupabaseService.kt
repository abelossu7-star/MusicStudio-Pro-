package com.musicstudio.pro.services.supabase

import android.content.Context
import com.musicstudio.pro.BuildConfig
import io.supabase.SupabaseClient
import io.supabase.gotrue.GoTrueClient
import io.supabase.postgrest.PostgrestClient
import io.supabase.storage.StorageClient

class SupabaseService(context: Context) {
    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    val client: SupabaseClient = SupabaseClient(
        supabaseUrl,
        supabaseKey,
        options = mapOf(
            "autoRefreshToken" to true,
            "persistSession" to true
        )
    )

    val auth: GoTrueClient = client.auth
    val database: PostgrestClient = client.database
    val storage: StorageClient = client.storage
}
