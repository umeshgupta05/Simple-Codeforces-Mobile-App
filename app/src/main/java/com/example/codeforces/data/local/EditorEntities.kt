package com.example.codeforces.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "editor_sessions")
data class EditorSession(
    @PrimaryKey
    val problemId: String,
    val code: String,
    val languageId: Int,
    val languageName: String,
    val customInput: String = "",
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_test_cases")
data class CustomTestCase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val problemId: String,
    val input: String,
    val expectedOutput: String,
    val createdAt: Long = System.currentTimeMillis()
)
