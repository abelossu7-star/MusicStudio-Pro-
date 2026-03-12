package com.musicstudio.pro.features.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstudio.pro.data.models.Song
import com.musicstudio.pro.data.models.UiState
import com.musicstudio.pro.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val feedState: StateFlow<UiState<List<Song>>> = _feedState

    fun loadFeed() {
        _feedState.value = UiState.Loading
        viewModelScope.launch {
            repository.fetchFeed().collect { result ->
                result.onSuccess { items ->
                    _feedState.value = UiState.Success(items)
                }.onFailure { error ->
                    _feedState.value = UiState.Error(error.localizedMessage ?: "Failed to load feed")
                }
            }
        }
    }
}
