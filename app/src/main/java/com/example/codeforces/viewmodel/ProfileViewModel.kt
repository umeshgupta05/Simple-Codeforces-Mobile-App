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

data class ProfileUiState(
    val user: CfUser? = null,
    val ratingHistory: List<UserRating> = emptyList(),
    val submissions: List<CfSubmission> = emptyList(),
    val blogEntries: List<BlogEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val username: String? = null
)

class ProfileViewModel(
    private val repository: CodeforcesRepository,
    private val username: String?
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(username = username))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        username?.let { loadProfile(it) }
    }

    fun loadProfile(handle: String) {
        if (handle.isBlank()) {
            _state.value = ProfileUiState(username = null)
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, username = handle)
            
            // Load user info
            repository.users(listOf(handle)).let { result ->
                if (result is Resource.Success && result.data.isNotEmpty()) {
                    _state.value = _state.value.copy(user = result.data.first())
                } else if (result is Resource.Error) {
                    _state.value = _state.value.copy(error = result.message)
                }
            }

            // Load rating history
            repository.getUserRating(handle).let { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(ratingHistory = result.data)
                }
            }

            // Load submissions (get more for statistics)
            repository.getUserStatus(handle, from = 1, count = 100).let { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(submissions = result.data)
                }
            }

            // Load blog entries
            repository.getUserBlogEntries(handle).let { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(blogEntries = result.data)
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun refresh() {
        _state.value.username?.let { loadProfile(it) }
    }
}

