package com.example.codeforces.data.model

import com.squareup.moshi.Json

data class RecentAction(
    @Json(name = "timeSeconds") val timeSeconds: Long,
    @Json(name = "blogEntry") val blogEntry: BlogEntry? = null,
    @Json(name = "comment") val comment: Comment? = null
)

