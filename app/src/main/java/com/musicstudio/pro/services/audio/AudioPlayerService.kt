package com.musicstudio.pro.services.audio

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class AudioPlayerService(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }
}
