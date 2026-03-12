package com.musicstudio.pro.features.upload.upload_video

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
class UploadVideoViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uploadState: StateFlow<UiState<String>> = _uploadState

    fun uploadVideo(fileUri: Uri) {
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
                _uploadState.value = UiState.Error("Failed to read video file")
                return@launch
            }

            val path = "videos/${UUID.randomUUID()}.mp4"
            val uploadResult = repository.uploadFile("videos", path, bytes).firstOrNull()
            val videoUrl = uploadResult?.getOrNull()

            if (videoUrl.isNullOrBlank()) {
                _uploadState.value = UiState.Error("Upload failed")
                return@launch
            }

            _uploadState.value = UiState.Success("Uploaded: $videoUrl")
        }
    }
}
