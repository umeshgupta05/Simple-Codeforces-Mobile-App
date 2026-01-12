package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.model.CfContest
import com.example.codeforces.data.model.ContestStandings
import com.example.codeforces.data.model.Hack
import com.example.codeforces.data.model.RatingChange
import com.example.codeforces.data.repository.CodeforcesRepository
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContestsUiState(
    val contests: List<CfContest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showGym: Boolean = false
)

data class ContestDetailUiState(
    val contest: CfContest? = null,
    val standings: ContestStandings? = null,
    val hacks: List<Hack> = emptyList(),
    val ratingChanges: List<RatingChange> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ContestsViewModel(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ContestsUiState())
    val state: StateFlow<ContestsUiState> = _state.asStateFlow()

    private val _detailState = MutableStateFlow(ContestDetailUiState())
    val detailState: StateFlow<ContestDetailUiState> = _detailState.asStateFlow()

    init {
        loadContests()
    }

    fun loadContests(gym: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, showGym = gym)
            when (val result = repository.contests(gym)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        contests = result.data,
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

    fun toggleGym() {
        loadContests(!_state.value.showGym)
    }

    fun loadContestDetail(contestId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            
            val contest = _state.value.contests.find { it.id == contestId }
            _detailState.value = _detailState.value.copy(contest = contest)

            // Load standings
            repository.getContestStandings(contestId, from = 1, count = 50).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(standings = result.data)
                }
            }

            // Load hacks
            repository.getContestHacks(contestId).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(hacks = result.data)
                }
            }

            // Load rating changes
            repository.getContestRatingChanges(contestId).let { result ->
                if (result is Resource.Success) {
                    _detailState.value = _detailState.value.copy(ratingChanges = result.data)
                }
            }

            _detailState.value = _detailState.value.copy(isLoading = false)
        }
    }
}

