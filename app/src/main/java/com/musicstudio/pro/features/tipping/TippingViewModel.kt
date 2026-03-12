package com.musicstudio.pro.features.tipping

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.models.UiState
import com.musicstudio.pro.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TippingViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    val receiverId = mutableStateOf("")
    val amount = mutableStateOf(0.0)

    private val _tipState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val tipState: StateFlow<UiState<Unit>> = _tipState

    fun sendTip() {
        val senderId = repository.currentUserId() ?: run {
            _tipState.value = UiState.Error("User not signed in")
            return
        }
        val receiver = receiverId.value.trim()
        if (receiver.isBlank()) {
            _tipState.value = UiState.Error("Receiver ID required")
            return
        }
        if (amount.value < 0.1) {
            _tipState.value = UiState.Error("Minimum tip is $0.10")
            return
        }

        viewModelScope.launch {
            _tipState.value = UiState.Loading
            repository.sendTip(senderId, receiver, amount.value).collect { result ->
                result.onSuccess {
                    _tipState.value = UiState.Success(Unit)
                }.onFailure {
                    _tipState.value = UiState.Error(it.localizedMessage ?: "Failed to send tip")
                }
            }
        }
    }
}
