package com.example.codeforces.data.model

import com.squareup.moshi.Json

data class BlogEntry(
    @Json(name = "id") val id: Int,
    @Json(name = "originalLocale") val originalLocale: String,
    @Json(name = "creationTimeSeconds") val creationTimeSeconds: Long,
    @Json(name = "authorHandle") val authorHandle: String,
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String? = null,
    @Json(name = "locale") val locale: String,
    @Json(name = "modificationTimeSeconds") val modificationTimeSeconds: Long,
    @Json(name = "allowViewHistory") val allowViewHistory: Boolean,
    @Json(name = "tags") val tags: List<String>,
    @Json(name = "rating") val rating: Int
)

data class Comment(
    @Json(name = "id") val id: Int,
    @Json(name = "creationTimeSeconds") val creationTimeSeconds: Long,
    @Json(name = "commentatorHandle") val commentatorHandle: String,
    @Json(name = "locale") val locale: String,
    @Json(name = "text") val text: String,
    @Json(name = "parentCommentId") val parentCommentId: Int? = null,
    @Json(name = "rating") val rating: Int
)

