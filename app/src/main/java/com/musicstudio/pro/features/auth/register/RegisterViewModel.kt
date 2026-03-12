package com.musicstudio.pro.features.auth.register

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
class RegisterViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val registerState: StateFlow<UiState<Unit>> = _registerState

    fun register(email: String, password: String) {
        _registerState.value = UiState.Loading
        viewModelScope.launch {
            repository.register(email, password).collect { result ->
                result.onSuccess {
                    _registerState.value = UiState.Success(Unit)
                }.onFailure {
                    _registerState.value = UiState.Error(it.localizedMessage ?: "Unknown error")
                }
            }
        }
    }
}
