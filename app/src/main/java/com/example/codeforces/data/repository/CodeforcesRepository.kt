package com.example.codeforces.data.repository

import com.example.codeforces.data.api.CodeforcesApiService
import com.example.codeforces.data.model.*
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CodeforcesRepository(
    private val api: CodeforcesApiService
) {
    // Problemset methods
    suspend fun recentStatus(count: Int = 30, problemsetName: String? = null): Resource<List<CfSubmission>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getRecentStatus(count, problemsetName)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    // Blog methods
    suspend fun getBlogEntryComments(blogEntryId: Int): Resource<List<Comment>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getBlogEntryComments(blogEntryId)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getBlogEntry(blogEntryId: Int): Resource<BlogEntry> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getBlogEntry(blogEntryId)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    // Contest methods
    suspend fun contests(gym: Boolean = false): Resource<List<CfContest>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getContestList(gym)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getContestHacks(contestId: Int, asManager: Boolean = false): Resource<List<Hack>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getContestHacks(contestId, asManager)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getContestRatingChanges(contestId: Int): Resource<List<RatingChange>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getContestRatingChanges(contestId)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getContestStandings(
        contestId: Int,
        from: Int = 1,
        count: Int = 20,
        handles: String? = null,
        room: Int? = null,
        showUnofficial: Boolean = false
    ): Resource<ContestStandings> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getContestStandings(contestId, from, count, handles, room, showUnofficial)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getContestStatus(
        contestId: Int,
        handle: String? = null,
        from: Int = 1,
        count: Int = 10
    ): Resource<List<CfSubmission>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getContestStatus(contestId, handle, from, count)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    // User methods
    suspend fun users(handles: List<String>): Resource<List<CfUser>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserInfo(handles.joinToString(";"))
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getUserBlogEntries(handle: String): Resource<List<BlogEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserBlogEntries(handle)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getUserFriends(onlyOnline: Boolean = false): Resource<List<String>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserFriends(onlyOnline)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getRatedList(
        activeOnly: Boolean = true,
        includeRetired: Boolean = false,
        contestId: Int? = null
    ): Resource<List<CfUser>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getRatedList(activeOnly, includeRetired, contestId)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getUserRating(handle: String): Resource<List<UserRating>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserRating(handle)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    suspend fun getUserStatus(
        handle: String,
        from: Int = 1,
        count: Int = 10
    ): Resource<List<CfSubmission>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserStatus(handle, from, count)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }

    // Recent Actions
    suspend fun getRecentActions(maxCount: Int = 100): Resource<List<RecentAction>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getRecentActions(maxCount)
                if (response.status == "OK" && response.result != null) {
                    Resource.Success(response.result)
                } else {
                    Resource.Error(response.comment ?: "Unknown error")
                }
            } catch (t: Throwable) {
                Resource.Error(t.message, t)
            }
        }
}

