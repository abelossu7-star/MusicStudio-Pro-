package com.musicstudio.pro.core

import com.musicstudio.pro.BuildConfig

object ElevenLabsConfig {
    /**
     * The ElevenLabs API key should be injected via gradle (local.properties or CI secret).
     * Do not commit secrets into source control.
     */
    val apiKey: String
        get() = BuildConfig.ELEVENLABS_API_KEY

    const val placeholder = "ELEVENLABS_API_KEY_NOT_SET"
}
