package com.musicstudio.pro.features.messaging.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.models.Message
import com.musicstudio.pro.data.models.UiState
import com.musicstudio.pro.data.repository.SupabaseRepository
import com.musicstudio.pro.services.realtime.RealtimeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    private val realtimeService: RealtimeService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    val messageText = mutableStateOf("")

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                realtimeService.subscribeToMessages(conversationId).collect { message ->
                    _messages.value = _messages.value + message
                }
                _uiState.value = UiState.Success(Unit)
            } catch (t: Throwable) {
                _uiState.value = UiState.Error(t.localizedMessage ?: "Failed to load messages")
            }
        }
    }

    fun sendMessage(receiverId: String) {
        val senderId = repository.currentUserId() ?: return
        val text = messageText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.sendMessage(
                Message(
                    id = "",
                    senderId = senderId,
                    receiverId = receiverId,
                    messageText = text
                )
            ).collect { result ->
                result.onSuccess {
                    messageText.value = ""
                    _uiState.value = UiState.Success(Unit)
                }.onFailure {
                    _uiState.value = UiState.Error(it.localizedMessage ?: "Failed to send message")
                }
            }
        }
    }
}
