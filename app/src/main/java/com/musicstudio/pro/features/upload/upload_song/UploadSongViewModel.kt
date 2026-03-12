package com.musicstudio.pro.features.upload.upload_song

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.models.UiState
import com.musicstudio.pro.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UploadSongViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uploadState: StateFlow<UiState<String>> = _uploadState
    val title = mutableStateOf("")
    val description = mutableStateOf("")
    val genre = mutableStateOf("")
    val mood = mutableStateOf("")

    fun uploadSong(fileUri: Uri) {
        val userId = repository.currentUserId()
        if (userId.isNullOrBlank()) {
            _uploadState.value = UiState.Error("User not signed in")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            val bytes = try {
                context.contentResolver.openInputStream(fileUri)?.use { it.readBytes() }
            } catch (t: Throwable) {
                null
            }

            if (bytes == null) {
                _uploadState.value = UiState.Error("Failed to read song file")
                return@launch
            }

            val path = "song_audio/${UUID.randomUUID()}.mp3"
            val uploadResult = repository.uploadFile("song_audio", path, bytes).firstOrNull()
            val audioUrl = uploadResult?.getOrNull()

            if (audioUrl.isNullOrBlank()) {
                _uploadState.value = UiState.Error("Upload failed")
                return@launch
            }

            val songResult = repository.createSong(
                userId = userId,
                title = title.value.ifBlank { "Untitled" },
                description = description.value,
                audioUrl = audioUrl,
                genre = genre.value.ifBlank { null },
                mood = mood.value.ifBlank { null }
            ).firstOrNull()

            songResult?.onSuccess {
                _uploadState.value = UiState.Success("Uploaded: ${it.title}")
            }?.onFailure {
                _uploadState.value = UiState.Error(it.localizedMessage ?: "Failed to save song")
            }
        }
    }
}
