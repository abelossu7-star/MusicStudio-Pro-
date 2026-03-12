package com.musicstudio.pro.features.studio

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.services.ai.AIService
import com.musicstudio.pro.services.audio.AudioPlayerService
import com.musicstudio.pro.services.elevenlabs.ElevenLabsService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val aiService: AIService,
    private val elevenLabsService: ElevenLabsService,
    private val audioPlayerService: AudioPlayerService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var prompt by mutableStateOf("")
        private set

    var lyrics by mutableStateOf<String?>(null)
        private set

    var beatUrl by mutableStateOf<String?>(null)
        private set

    var voiceId by mutableStateOf("")
        private set

    var ttsText by mutableStateOf("")
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onPromptChanged(value: String) {
        prompt = value
    }

    fun onVoiceIdChanged(value: String) {
        voiceId = value
    }

    fun onTtsTextChanged(value: String) {
        ttsText = value
    }

    fun generateLyrics() {
        if (prompt.isBlank()) {
            statusMessage = "Enter a prompt to generate lyrics."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            lyrics = try {
                aiService.generateLyrics(prompt)
            } catch (t: Throwable) {
                statusMessage = "Failed to generate lyrics: ${t.message}"
                null
            }
            isLoading = false
        }
    }

    fun generateBeat() {
        if (prompt.isBlank()) {
            statusMessage = "Enter a prompt to generate a beat."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            beatUrl = try {
                aiService.generateBeat(prompt)
            } catch (t: Throwable) {
                statusMessage = "Failed to generate beat: ${t.message}"
                null
            }
            isLoading = false
        }
    }

    fun playBeat() {
        beatUrl?.let { audioPlayerService.play(it) }
    }

    fun synthesizeVoice() {
        if (voiceId.isBlank() || ttsText.isBlank()) {
            statusMessage = "Enter a voice ID and the text to synthesize."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null

            val bytes = try {
                elevenLabsService.synthesizeSpeech(voiceId, ttsText)
            } catch (t: Throwable) {
                statusMessage = "Failed to synthesize voice: ${t.message}"
                null
            }

            if (bytes == null || bytes.isEmpty()) {
                statusMessage = statusMessage ?: "No audio returned."
                isLoading = false
                return@launch
            }

            val fileName = "elevenlabs_tts_${System.currentTimeMillis()}.mp3"
            val file = writeBytesToCache(bytes, fileName)

            if (file == null) {
                statusMessage = "Unable to write audio file."
                isLoading = false
                return@launch
            }

            audioPlayerService.play(file.toURI().toString())
            statusMessage = "Playing synthesized audio."
            isLoading = false
        }
    }

    private fun writeBytesToCache(bytes: ByteArray, fileName: String): File? {
        return try {
            val file = File(context.cacheDir, fileName)
            file.writeBytes(bytes)
            file
        } catch (t: Throwable) {
            null
        }
    }
}
