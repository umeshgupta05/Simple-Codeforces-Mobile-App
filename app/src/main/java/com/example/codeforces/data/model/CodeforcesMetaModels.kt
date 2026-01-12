package com.example.codeforces.data.model

import com.squareup.moshi.Json

data class CfApiResponse<T>(
    @Json(name = "status") val status: String,
    @Json(name = "comment") val comment: String? = null,
    @Json(name = "result") val result: T? = null
)

data class CfContest(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String,
    @Json(name = "phase") val phase: String,
    @Json(name = "frozen") val frozen: Boolean,
    @Json(name = "durationSeconds") val durationSeconds: Long,
    @Json(name = "startTimeSeconds") val startTimeSeconds: Long?
)

data class CfUser(
    @Json(name = "handle") val handle: String,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "rating") val rating: Int? = null,
    @Json(name = "maxRating") val maxRating: Int? = null,
    @Json(name = "rank") val rank: String? = null,
    @Json(name = "maxRank") val maxRank: String? = null,
    @Json(name = "contribution") val contribution: Int? = null,
    @Json(name = "friendOfCount") val friendOfCount: Int? = null
)

data class CfSubmission(
    @Json(name = "id") val id: Long,
    @Json(name = "contestId") val contestId: Int?,
    @Json(name = "creationTimeSeconds") val creationTimeSeconds: Long,
    @Json(name = "relativeTimeSeconds") val relativeTimeSeconds: Long,
    @Json(name = "problem") val problem: ProblemDto,
    @Json(name = "programmingLanguage") val programmingLanguage: String,
    @Json(name = "verdict") val verdict: String?,
    @Json(name = "timeConsumedMillis") val timeConsumedMillis: Int,
    @Json(name = "memoryConsumedBytes") val memoryConsumedBytes: Long
)

