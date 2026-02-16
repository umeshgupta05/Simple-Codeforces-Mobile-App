package com.example.codeforces.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "problem_notes")
data class ProblemNote(
    @PrimaryKey
    val problemId: String,
    val note: String,
    val lastModified: Long = System.currentTimeMillis()
)
