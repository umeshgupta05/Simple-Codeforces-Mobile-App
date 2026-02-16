package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.Problem
import com.example.codeforces.data.repository.ProblemRepository
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortBy {
    DEFAULT, RATING_ASC, RATING_DESC, SOLVED_ASC, SOLVED_DESC
}

data class ProblemFilter(
    val tags: List<String> = emptyList(),
    val minRating: Int? = null,
    val maxRating: Int? = null,
    val search: String = "",
    val sortBy: SortBy = SortBy.DEFAULT
)

data class HomeUiState(
    val problems: List<Problem> = emptyList(),
    val allProblems: List<Problem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: ProblemFilter = ProblemFilter()
)

class HomeViewModel(
    private val repository: ProblemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = _state

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.loadProblemset(_state.value.filter.tags)) {
                is Resource.Success -> _state.value = _state.value.copy(
                    allProblems = result.data,
                    problems = applyFilters(result.data, _state.value.filter),
                    isLoading = false,
                    error = null
                )

                is Resource.Error -> _state.value =
                    _state.value.copy(isLoading = false, error = result.message)

                Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun updateFilter(filter: ProblemFilter) {
        val source = _state.value.allProblems.ifEmpty { _state.value.problems }
        val filtered = applyFilters(source, filter)
        _state.value = _state.value.copy(filter = filter, problems = filtered)
    }

    private fun applyFilters(
        problems: List<Problem>,
        filter: ProblemFilter
    ): List<Problem> {
        val filtered = problems.filter { problem ->
            val ratingOk =
                (filter.minRating == null || (problem.rating ?: Int.MIN_VALUE) >= filter.minRating) &&
                    (filter.maxRating == null || (problem.rating ?: Int.MAX_VALUE) <= filter.maxRating)

            val tagOk =
                filter.tags.isEmpty() || problem.tags.any { it in filter.tags }

            val textOk = filter.search.isBlank() ||
                problem.name.contains(filter.search, ignoreCase = true) ||
                problem.id.contains(filter.search, ignoreCase = true)

            ratingOk && tagOk && textOk
        }

        return when (filter.sortBy) {
            SortBy.DEFAULT -> filtered
            SortBy.RATING_ASC -> filtered.sortedBy { it.rating ?: 0 }
            SortBy.RATING_DESC -> filtered.sortedByDescending { it.rating ?: 0 }
            SortBy.SOLVED_ASC -> filtered.sortedBy { it.solvedCount ?: 0L }
            SortBy.SOLVED_DESC -> filtered.sortedByDescending { it.solvedCount ?: 0L }
        }
    }
}
