package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.local.BookmarkDao
import com.example.codeforces.data.local.BookmarkedProblem
import com.example.codeforces.data.model.Problem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BookmarksUiState(
    val bookmarks: List<BookmarkedProblem> = emptyList()
)

class BookmarksViewModel(
    private val bookmarkDao: BookmarkDao
) : ViewModel() {

    val bookmarks: StateFlow<List<BookmarkedProblem>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isBookmarked(problemId: String) = bookmarkDao.isBookmarked(problemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleBookmark(problem: Problem) {
        viewModelScope.launch {
            val exists = bookmarks.value.any { it.problemId == problem.id }
            if (exists) {
                bookmarkDao.deleteByProblemId(problem.id)
            } else {
                bookmarkDao.insert(
                    BookmarkedProblem(
                        problemId = problem.id,
                        name = problem.name,
                        contestId = problem.contestId,
                        index = problem.index,
                        rating = problem.rating,
                        tags = problem.tags.joinToString(",")
                    )
                )
            }
        }
    }

    fun toggleBookmarkById(problemId: String, name: String, contestId: Int?, index: String, rating: Int?, tags: List<String>) {
        viewModelScope.launch {
            val exists = bookmarks.value.any { it.problemId == problemId }
            if (exists) {
                bookmarkDao.deleteByProblemId(problemId)
            } else {
                bookmarkDao.insert(
                    BookmarkedProblem(
                        problemId = problemId,
                        name = name,
                        contestId = contestId,
                        index = index,
                        rating = rating,
                        tags = tags.joinToString(",")
                    )
                )
            }
        }
    }

    fun removeBookmark(problemId: String) {
        viewModelScope.launch {
            bookmarkDao.deleteByProblemId(problemId)
        }
    }
}
