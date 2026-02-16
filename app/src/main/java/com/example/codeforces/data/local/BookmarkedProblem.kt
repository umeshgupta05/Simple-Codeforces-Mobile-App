package com.example.codeforces.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_problems")
data class BookmarkedProblem(
    @PrimaryKey
    val problemId: String,
    val name: String,
    val contestId: Int?,
    val index: String,
    val rating: Int?,
    val tags: String = "",        // comma-separated
    val bookmarkedAt: Long = System.currentTimeMillis()
)
