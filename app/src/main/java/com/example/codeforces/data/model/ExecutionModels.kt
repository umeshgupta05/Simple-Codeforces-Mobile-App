package com.example.codeforces.data.model

data class LanguageTemplate(
    val id: Int,
    val name: String,
    val boilerplate: String
)

data class ExecutionRequest(
    val sourceCode: String,
    val languageId: Int,
    val stdin: String?,
    val problemId: String
)

data class ExecutionResult(
    val stdout: String?,
    val stderr: String?,
    val status: ExecutionStatus,
    val timeSeconds: Double?,
    val memoryKb: Int?,
    val compileOutput: String? = null
)

data class ExecutionStatus(
    val code: Int,
    val description: String
)

data class TestCase(
    val id: String,
    val input: String,
    val expectedOutput: String,
    val isCommunity: Boolean = false,
    val votes: Int = 0,
    val author: String? = null,
    val note: String? = null
)

