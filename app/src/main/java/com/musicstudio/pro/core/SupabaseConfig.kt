package com.musicstudio.pro.core

import com.musicstudio.pro.BuildConfig

object SupabaseConfig {
    val url: String
        get() = BuildConfig.SUPABASE_URL

    val anonKey: String
        get() = BuildConfig.SUPABASE_KEY

    /**
     * Service role key should never be packaged into the client app.
     * Use this placeholder as a reminder to keep it secret and only use it on trusted server/edge functions.
     */
    const val serviceRoleKeyPlaceholder = "sb_secret_XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
}
