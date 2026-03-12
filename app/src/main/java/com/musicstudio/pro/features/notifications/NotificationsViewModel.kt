package com.musicstudio.pro.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.models.NotificationItem
import com.musicstudio.pro.data.models.UiState
import com.musicstudio.pro.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<UiState<List<NotificationItem>>>(UiState.Loading)
    val notifications: StateFlow<UiState<List<NotificationItem>>> = _notifications

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = UiState.Loading
            val userId = repository.currentUserId()
            if (userId.isNullOrBlank()) {
                _notifications.value = UiState.Error("User not signed in")
                return@launch
            }

            repository.fetchNotifications(userId).collect { result ->
                result.onSuccess { list ->
                    _notifications.value = UiState.Success(list)
                }.onFailure {
                    _notifications.value = UiState.Error(it.localizedMessage ?: "Failed to load notifications")
                }
            }
        }
    }
}
