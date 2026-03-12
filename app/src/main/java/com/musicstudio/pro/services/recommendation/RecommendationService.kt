package com.musicstudio.pro.services.recommendation

import com.musicstudio.pro.data.models.Song

/**
 * A simple recommendation service that ranks songs based on plays, likes, comments, and recency.
 * Note: This is a client-side heuristic and should be replaced with a server-side implementation for real traffic.
 */
class RecommendationService {
    /**
     * Calculates a score for a song based on a weighted formula.
     */
    fun scoreSong(song: Song): Double {
        val plays = song.plays?.toDouble() ?: 0.0
        val likes = song.likes?.toDouble() ?: 0.0
        val comments = song.comments?.toDouble() ?: 0.0
        val recency = if (song.createdAt.isNullOrBlank()) 0.0 else 1.0
        return plays * 0.4 + likes * 0.3 + comments * 0.2 + recency * 0.1
    }
}
