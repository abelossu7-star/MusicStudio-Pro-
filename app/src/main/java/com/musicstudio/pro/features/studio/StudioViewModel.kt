package com.musicstudio.pro.features.studio

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.repository.SupabaseRepository
import com.musicstudio.pro.services.ai.AIService
import com.musicstudio.pro.services.audio.AudioPlayerService
import com.musicstudio.pro.services.elevenlabs.ElevenLabsService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val aiService: AIService,
    private val elevenLabsService: ElevenLabsService,
    private val supabaseRepository: SupabaseRepository,
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

    var voiceSampleLocalUri by mutableStateOf<String?>(null)
        private set

    var voiceSampleRemoteUrl by mutableStateOf<String?>(null)
        private set

    var isRecording by mutableStateOf(false)
        private set

    var recordedFileUri by mutableStateOf<String?>(null)
        private set

    var clonedVoiceUrl by mutableStateOf<String?>(null)
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
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

    fun onVoiceSampleLocalUriChanged(value: String?) {
        voiceSampleLocalUri = value
    }

    fun onVoiceSampleRemoteUrlChanged(value: String?) {
        voiceSampleRemoteUrl = value
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

    fun generateSong() {
        if (prompt.isBlank()) {
            statusMessage = "Enter a prompt to generate a song."
            return
        }

        val userId = supabaseRepository.currentUserId()
        if (userId.isNullOrBlank()) {
            statusMessage = "User not signed in." 
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null

            // Generate lyrics + beat
            val generatedLyrics = try {
                aiService.generateLyrics(prompt)
            } catch (t: Throwable) {
                statusMessage = "Failed to generate lyrics: ${t.message}"
                isLoading = false
                return@launch
            }

            val generatedBeatUrl = try {
                aiService.generateBeat(prompt)
            } catch (t: Throwable) {
                statusMessage = "Failed to generate beat: ${t.message}"
                isLoading = false
                return@launch
            }

            // Download beat and upload to Supabase
            val beatUploadUrl = downloadAndUploadFile(
                sourceUrl = generatedBeatUrl,
                bucket = "ai_generated_music",
                path = "beats/${UUID.randomUUID()}.mp3"
            )

            if (beatUploadUrl == null) {
                statusMessage = "Failed to upload generated beat."
                isLoading = false
                return@launch
            }

            // Synthesize vocals and upload
            // Optionally synthesize vocals if user provided voice fields.
            val voiceUrl = if (voiceId.isNotBlank() && ttsText.isNotBlank()) {
                val voiceBytes = try {
                    elevenLabsService.synthesizeSpeech(voiceId, ttsText)
                } catch (_: Throwable) {
                    null
                }

                voiceBytes?.takeIf { it.isNotEmpty() }?.let {
                    uploadBytesToSupabase(it, "ai_generated_music", "vocals/${UUID.randomUUID()}.mp3")
                }
            } else {
                null
            }

            val audioUrl = voiceUrl ?: beatUploadUrl

            // Create song record
            val songResult = supabaseRepository.createSong(
                userId = userId,
                title = prompt.takeIf { it.isNotBlank() } ?: "AI Song",
                description = generatedLyrics,
                audioUrl = audioUrl
            ).firstOrNull()

            songResult?.onSuccess {
                statusMessage = "Song created: ${it.audioUrl}"
            }?.onFailure {
                statusMessage = "Failed to save song metadata: ${it.message}"
            }

            isLoading = false
        }
    }

    private var recorder: MediaRecorder? = null

    fun startRecording() {
        if (isRecording) return

        val file = File(context.cacheDir, "recording_${System.currentTimeMillis()}.m4a")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                recordedFileUri = Uri.fromFile(file).toString()
                statusMessage = "Recording..."
            } catch (t: Throwable) {
                statusMessage = "Failed to start recording: ${t.message}"
                release()
                recorder = null
            }
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        try {
            recorder?.apply {
                stop()
                release()
            }
            statusMessage = "Recording stopped."
        } catch (t: Throwable) {
            statusMessage = "Failed to stop recording: ${t.message}"
        } finally {
            recorder = null
            isRecording = false
        }
    }

    fun cloneVoiceSample() {
        val sampleUrl = voiceSampleRemoteUrl ?: recordedFileUri ?: voiceSampleLocalUri
        if (sampleUrl.isNullOrBlank()) {
            statusMessage = "Provide a voice sample (URL/file) to clone."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null

            clonedVoiceUrl = try {
                aiService.cloneVoice(sampleUrl)
            } catch (t: Throwable) {
                statusMessage = "Failed to clone voice: ${t.message}"
                null
            }

            clonedVoiceUrl?.let { url ->
                statusMessage = "Cloned voice ready to play."
                playUrl(url)
            }

            isLoading = false
        }
    }

    fun playRecording() {
        val uri = recordedFileUri ?: return
        playUrl(uri)
    }

    fun playClonedVoice() {
        val uri = clonedVoiceUrl ?: return
        playUrl(uri)
    }

    fun stopPlayback() {
        audioPlayerService.stop()
        isPlaying = false
        statusMessage = "Playback stopped."
    }

    private fun playUrl(url: String) {
        audioPlayerService.play(url)
        isPlaying = true
        statusMessage = "Playing..."
    }

    fun uploadVoiceSample(uriString: String) {
        if (uriString.isBlank()) {
            statusMessage = "No sample URI provided."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            voiceSampleLocalUri = uriString

            val bytes = try {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (t: Throwable) {
                null
            }

            if (bytes == null) {
                statusMessage = "Failed to read voice sample."
                isLoading = false
                return@launch
            }

            val uploadedUrl = uploadBytesToSupabase(
                bytes,
                "voice_samples",
                "samples/${UUID.randomUUID()}.mp3"
            )

            voiceSampleRemoteUrl = uploadedUrl
            statusMessage = if (uploadedUrl != null) "Voice sample uploaded." else "Upload failed."
            isLoading = false
        }
    }

    private suspend fun downloadAndUploadFile(sourceUrl: String, bucket: String, path: String): String? {
        return try {
            val bytes = downloadUrl(sourceUrl)
            bytes?.let { uploadBytesToSupabase(it, bucket, path) }
        } catch (_: Throwable) {
            null
        }
    }

    private suspend fun uploadBytesToSupabase(bytes: ByteArray, bucket: String, path: String): String? {
        val result = supabaseRepository.uploadFile(bucket, path, bytes).firstOrNull()
        return result?.getOrNull()
    }

    private suspend fun downloadUrl(url: String): ByteArray? {
        return try {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient().newCall(request).execute()
            if (!response.isSuccessful) return null
            response.body?.bytes()
        } catch (_: Throwable) {
            null
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
