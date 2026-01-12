package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.CfSubmission
import com.example.codeforces.data.repository.CodeforcesRepository
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubmissionsUiState(
    val recentSubmissions: List<CfSubmission> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val count: Int = 50
)

class SubmissionsViewModel(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubmissionsUiState())
    val state: StateFlow<SubmissionsUiState> = _state.asStateFlow()

    init {
        loadRecentSubmissions()
    }

    fun loadRecentSubmissions(count: Int = 50) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, count = count)
            when (val result = repository.recentStatus(count)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        recentSubmissions = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    // Already handled by setting isLoading = true
                }
            }
        }
    }

    fun refresh() {
        loadRecentSubmissions(_state.value.count)
    }
}

