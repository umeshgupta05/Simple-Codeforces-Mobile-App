package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.BlogEntry
import com.example.codeforces.data.model.Comment
import com.example.codeforces.data.model.RecentAction
import com.example.codeforces.data.repository.CodeforcesRepository
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BlogsUiState(
    val recentActions: List<RecentAction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class BlogDetailUiState(
    val blogEntry: BlogEntry? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class BlogsViewModel(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BlogsUiState())
    val state: StateFlow<BlogsUiState> = _state.asStateFlow()

    private val _detailState = MutableStateFlow(BlogDetailUiState())
    val detailState: StateFlow<BlogDetailUiState> = _detailState.asStateFlow()

    init {
        loadRecentActions()
    }

    fun loadRecentActions(maxCount: Int = 100) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getRecentActions(maxCount)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        recentActions = result.data,
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

    fun loadBlogEntry(blogEntryId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            
            // Load blog entry
            repository.getBlogEntry(blogEntryId).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(blogEntry = result.data)
                } else if (result is Resource.Error) {
                    _detailState.value = _detailState.value.copy(error = result.message)
                }
            }

            // Load comments
            repository.getBlogEntryComments(blogEntryId).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(comments = result.data)
                }
            }

            _detailState.value = _detailState.value.copy(isLoading = false)
        }
    }
}

