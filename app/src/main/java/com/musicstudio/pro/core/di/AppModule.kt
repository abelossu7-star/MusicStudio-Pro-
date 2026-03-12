package com.musicstudio.pro.core.di

import android.content.Context
import com.musicstudio.pro.data.repository.SupabaseRepository
import com.musicstudio.pro.services.ai.AIService
import com.musicstudio.pro.services.audio.AudioPlayerService
import com.musicstudio.pro.services.elevenlabs.ElevenLabsService
import com.musicstudio.pro.services.media.MediaCompressionService
import com.musicstudio.pro.services.realtime.RealtimeService
import com.musicstudio.pro.services.supabase.SupabaseService
import com.musicstudio.pro.services.video.VideoRecorderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseService(@ApplicationContext context: Context): SupabaseService {
        return SupabaseService(context)
    }

    @Provides
    @Singleton
    fun provideSupabaseRepository(supabaseService: SupabaseService): SupabaseRepository {
        return SupabaseRepository(supabaseService)
    }

    @Provides
    @Singleton
    fun provideAudioPlayerService(@ApplicationContext context: Context): AudioPlayerService {
        return AudioPlayerService(context)
    }

    @Provides
    @Singleton
    fun provideVideoRecorderService(@ApplicationContext context: Context): VideoRecorderService {
        return VideoRecorderService(context)
    }

    @Provides
    @Singleton
    fun provideRealtimeService(
        @ApplicationContext context: Context,
        supabaseService: SupabaseService
    ): RealtimeService {
        return RealtimeService(context, supabaseService)
    }

    @Provides
    @Singleton
    fun provideAiService(): AIService {
        return AIService()
    }

    @Provides
    @Singleton
    fun provideElevenLabsService(): ElevenLabsService {
        return ElevenLabsService()
    }

    @Provides
    @Singleton
    fun provideMediaCompressionService(): MediaCompressionService {
        return MediaCompressionService()
    }

    @Provides
    @Singleton
    fun provideRecommendationService(): com.musicstudio.pro.services.recommendation.RecommendationService {
        return com.musicstudio.pro.services.recommendation.RecommendationService()
    }
}
