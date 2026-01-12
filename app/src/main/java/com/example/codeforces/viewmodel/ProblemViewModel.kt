package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.Problem
import com.example.codeforces.data.model.TestCase
import com.example.codeforces.data.repository.ProblemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProblemDetailUiState(
    val problem: Problem? = null,
    val tests: List<TestCase> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProblemViewModel(
    private val problemRepository: ProblemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProblemDetailUiState())
    val state: StateFlow<ProblemDetailUiState> = _state

    fun load(problemId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val problem = problemRepository.findProblem(problemId)
            _state.value = _state.value.copy(problem = problem, isLoading = false)
        }
    }
}

