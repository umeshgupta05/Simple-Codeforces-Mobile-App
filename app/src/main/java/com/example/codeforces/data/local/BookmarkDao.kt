package com.example.codeforces.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarked_problems ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedProblem>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_problems WHERE problemId = :problemId)")
    fun isBookmarked(problemId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkedProblem)

    @Query("DELETE FROM bookmarked_problems WHERE problemId = :problemId")
    suspend fun deleteByProblemId(problemId: String)
}
