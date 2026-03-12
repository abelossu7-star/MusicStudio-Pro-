package com.musicstudio.pro.features.auth.login

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
class LoginViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val loginState: StateFlow<UiState<Unit>> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                result.onSuccess {
                    _loginState.value = UiState.Success(Unit)
                }.onFailure {
                    _loginState.value = UiState.Error(it.localizedMessage ?: "Unknown error")
                }
            }
        }
    }
}
