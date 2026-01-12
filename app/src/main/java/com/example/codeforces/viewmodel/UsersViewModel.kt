package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.BlogEntry
import com.example.codeforces.data.model.CfSubmission
import com.example.codeforces.data.model.CfUser
import com.example.codeforces.data.model.UserRating
import com.example.codeforces.data.repository.CodeforcesRepository
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<CfUser> = emptyList(),
    val searchHandle: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class UserDetailUiState(
    val user: CfUser? = null,
    val ratingHistory: List<UserRating> = emptyList(),
    val submissions: List<CfSubmission> = emptyList(),
    val blogEntries: List<BlogEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UsersViewModel(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UsersUiState())
    val state: StateFlow<UsersUiState> = _state.asStateFlow()

    private val _detailState = MutableStateFlow(UserDetailUiState())
    val detailState: StateFlow<UserDetailUiState> = _detailState.asStateFlow()

    fun searchUser(handle: String) {
        if (handle.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, searchHandle = handle)
            when (val result = repository.users(listOf(handle))) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        users = result.data,
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

    fun loadRatedList(activeOnly: Boolean = true) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getRatedList(activeOnly)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        users = result.data,
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

    fun loadUserDetail(handle: String) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            
            // Load user info
            repository.users(listOf(handle)).let { result ->
                if (result is Resource.Success && result.data.isNotEmpty()) {
                    _detailState.value = _detailState.value.copy(user = result.data.first())
                }
            }

            // Load rating history
            repository.getUserRating(handle).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(ratingHistory = result.data)
                }
            }

            // Load submissions
            repository.getUserStatus(handle, from = 1, count = 50).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(submissions = result.data)
                }
            }

            // Load blog entries
            repository.getUserBlogEntries(handle).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(blogEntries = result.data)
                }
            }

            _detailState.value = _detailState.value.copy(isLoading = false)
        }
    }
}

