package com.example.codeforces.data.model

import com.squareup.moshi.Json

data class ProblemDto(
    @Json(name = "contestId") val contestId: Int?,
    @Json(name = "index") val index: String,
    @Json(name = "name") val name: String,
    @Json(name = "rating") val rating: Int?,
    @Json(name = "tags") val tags: List<String>
)

data class ProblemStatisticsDto(
    @Json(name = "contestId") val contestId: Int?,
    @Json(name = "index") val index: String,
    @Json(name = "solvedCount") val solvedCount: Long
)

data class ProblemsetResponse(
    @Json(name = "status") val status: String,
    @Json(name = "result") val result: ProblemsetResult
)

data class ProblemsetResult(
    @Json(name = "problems") val problems: List<ProblemDto>,
    @Json(name = "problemStatistics") val statistics: List<ProblemStatisticsDto>
)

data class Problem(
    val id: String,
    val contestId: Int?,
    val index: String,
    val name: String,
    val rating: Int?,
    val tags: List<String>,
    val solvedCount: Long? = null,
    val timeLimitMillis: Int? = null,
    val memoryLimitMb: Int? = null,
    val statementHtml: String? = null,
    val samples: List<SampleTest> = emptyList()
)

data class SampleTest(
    val input: String,
    val output: String,
    val explanation: String? = null
)

