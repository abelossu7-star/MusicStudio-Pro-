package com.musicstudio.pro.features.profile.edit_profile

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
class EditProfileViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    val username = mutableStateOf("")
    val bio = mutableStateOf("")
    val profileImageUrl = mutableStateOf("")

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val state: StateFlow<UiState<Unit>> = _state

    fun loadUser() {
        val userId = repository.currentUserId() ?: return
        viewModelScope.launch {
            repository.fetchUser(userId).collect { result ->
                result.onSuccess { user ->
                    username.value = user.username.orEmpty()
                    bio.value = user.bio.orEmpty()
                    profileImageUrl.value = user.profileImage.orEmpty()
                    _state.value = UiState.Success(Unit)
                }.onFailure {
                    _state.value = UiState.Error(it.localizedMessage ?: "Failed to load profile")
                }
            }
        }
    }

    fun saveProfile() {
        val userId = repository.currentUserId() ?: return
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.updateUserProfile(
                userId = userId,
                username = username.value.takeIf { it.isNotBlank() },
                bio = bio.value.takeIf { it.isNotBlank() },
                profileImage = profileImageUrl.value.takeIf { it.isNotBlank() }
            ).collect { result ->
                result.onSuccess {
                    _state.value = UiState.Success(Unit)
                }.onFailure {
                    _state.value = UiState.Error(it.localizedMessage ?: "Failed to save profile")
                }
            }
        }
    }
}
