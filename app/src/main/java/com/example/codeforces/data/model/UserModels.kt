package com.example.codeforces.data.model

import com.squareup.moshi.Json

// CfUser already exists in CodeforcesMetaModels.kt
// Adding user-specific response models

data class UserRating(
    @Json(name = "contestId") val contestId: Int,
    @Json(name = "contestName") val contestName: String,
    @Json(name = "handle") val handle: String,
    @Json(name = "rank") val rank: Int,
    @Json(name = "ratingUpdateTimeSeconds") val ratingUpdateTimeSeconds: Long,
    @Json(name = "oldRating") val oldRating: Int,
    @Json(name = "newRating") val newRating: Int
)

// User status (submissions) - CfSubmission already exists
// User blog entries - BlogEntry already exists

