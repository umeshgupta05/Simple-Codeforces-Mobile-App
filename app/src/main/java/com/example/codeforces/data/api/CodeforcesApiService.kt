package com.example.codeforces.data.api

import com.example.codeforces.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeforcesApiService {
    // Problemset APIs
    @GET("problemset.problems")
    suspend fun getProblemset(
        @Query("tags") tags: String? = null
    ): ProblemsetResponse

    @GET("problemset.recentStatus")
    suspend fun getRecentStatus(
        @Query("count") count: Int = 30,
        @Query("problemsetName") problemsetName: String? = null
    ): CfApiResponse<List<CfSubmission>>

    // Blog APIs
    @GET("blogEntry.comments")
    suspend fun getBlogEntryComments(
        @Query("blogEntryId") blogEntryId: Int
    ): CfApiResponse<List<Comment>>

    @GET("blogEntry.view")
    suspend fun getBlogEntry(
        @Query("blogEntryId") blogEntryId: Int
    ): CfApiResponse<BlogEntry>

    // Contest APIs
    @GET("contest.list")
    suspend fun getContestList(
        @Query("gym") gym: Boolean = false
    ): CfApiResponse<List<CfContest>>

    @GET("contest.hacks")
    suspend fun getContestHacks(
        @Query("contestId") contestId: Int,
        @Query("asManager") asManager: Boolean = false
    ): CfApiResponse<List<Hack>>

    @GET("contest.ratingChanges")
    suspend fun getContestRatingChanges(
        @Query("contestId") contestId: Int
    ): CfApiResponse<List<RatingChange>>

    @GET("contest.standings")
    suspend fun getContestStandings(
        @Query("contestId") contestId: Int,
        @Query("from") from: Int = 1,
        @Query("count") count: Int = 20,
        @Query("handles") handles: String? = null,
        @Query("room") room: Int? = null,
        @Query("showUnofficial") showUnofficial: Boolean = false
    ): CfApiResponse<ContestStandings>

    @GET("contest.status")
    suspend fun getContestStatus(
        @Query("contestId") contestId: Int,
        @Query("handle") handle: String? = null,
        @Query("from") from: Int = 1,
        @Query("count") count: Int = 10
    ): CfApiResponse<List<CfSubmission>>

    // User APIs
    @GET("user.info")
    suspend fun getUserInfo(
        @Query("handles") handles: String
    ): CfApiResponse<List<CfUser>>

    @GET("user.blogEntries")
    suspend fun getUserBlogEntries(
        @Query("handle") handle: String
    ): CfApiResponse<List<BlogEntry>>

    @GET("user.friends")
    suspend fun getUserFriends(
        @Query("onlyOnline") onlyOnline: Boolean = false
    ): CfApiResponse<List<String>>

    @GET("user.ratedList")
    suspend fun getRatedList(
        @Query("activeOnly") activeOnly: Boolean = true,
        @Query("includeRetired") includeRetired: Boolean = false,
        @Query("contestId") contestId: Int? = null
    ): CfApiResponse<List<CfUser>>

    @GET("user.rating")
    suspend fun getUserRating(
        @Query("handle") handle: String
    ): CfApiResponse<List<UserRating>>

    @GET("user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String,
        @Query("from") from: Int = 1,
        @Query("count") count: Int = 10
    ): CfApiResponse<List<CfSubmission>>

    // Recent Actions API
    @GET("recentActions")
    suspend fun getRecentActions(
        @Query("maxCount") maxCount: Int = 100
    ): CfApiResponse<List<RecentAction>>
}

