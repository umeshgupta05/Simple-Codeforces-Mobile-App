package com.example.codeforces.data.model

import com.squareup.moshi.Json

data class Hack(
    @Json(name = "id") val id: Long,
    @Json(name = "creationTimeSeconds") val creationTimeSeconds: Long,
    @Json(name = "hacker") val hacker: Party,
    @Json(name = "defender") val defender: Party,
    @Json(name = "verdict") val verdict: String?,
    @Json(name = "problem") val problem: ProblemDto,
    @Json(name = "test") val test: String? = null,
    @Json(name = "judgeProtocol") val judgeProtocol: JudgeProtocol? = null
)

data class JudgeProtocol(
    @Json(name = "manual") val manual: String,
    @Json(name = "protocol") val protocol: String,
    @Json(name = "verdict") val verdict: String
)

data class RatingChange(
    @Json(name = "contestId") val contestId: Int,
    @Json(name = "contestName") val contestName: String,
    @Json(name = "handle") val handle: String,
    @Json(name = "rank") val rank: Int,
    @Json(name = "ratingUpdateTimeSeconds") val ratingUpdateTimeSeconds: Long,
    @Json(name = "oldRating") val oldRating: Int,
    @Json(name = "newRating") val newRating: Int
)

data class ContestStandings(
    @Json(name = "contest") val contest: CfContest,
    @Json(name = "problems") val problems: List<ProblemDto>,
    @Json(name = "rows") val rows: List<RanklistRow>
)

data class RanklistRow(
    @Json(name = "party") val party: Party,
    @Json(name = "rank") val rank: Int,
    @Json(name = "points") val points: Double,
    @Json(name = "penalty") val penalty: Int,
    @Json(name = "successfulHackCount") val successfulHackCount: Int,
    @Json(name = "unsuccessfulHackCount") val unsuccessfulHackCount: Int,
    @Json(name = "problemResults") val problemResults: List<ProblemResult>
)

data class ProblemResult(
    @Json(name = "points") val points: Double,
    @Json(name = "penalty") val penalty: Int? = null,
    @Json(name = "rejectedAttemptCount") val rejectedAttemptCount: Int,
    @Json(name = "type") val type: String,
    @Json(name = "bestSubmissionTimeSeconds") val bestSubmissionTimeSeconds: Int? = null
)

data class Party(
    @Json(name = "contestId") val contestId: Int? = null,
    @Json(name = "members") val members: List<Member>,
    @Json(name = "participantType") val participantType: String,
    @Json(name = "teamId") val teamId: Int? = null,
    @Json(name = "teamName") val teamName: String? = null,
    @Json(name = "ghost") val ghost: Boolean,
    @Json(name = "room") val room: Int? = null,
    @Json(name = "startTimeSeconds") val startTimeSeconds: Int? = null
)

data class Member(
    @Json(name = "handle") val handle: String,
    @Json(name = "name") val name: String? = null
)

