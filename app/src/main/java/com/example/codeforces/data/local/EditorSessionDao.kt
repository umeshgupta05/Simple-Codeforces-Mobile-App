package com.example.codeforces.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EditorSessionDao {
    @Query("SELECT * FROM editor_sessions WHERE problemId = :problemId")
    suspend fun getSession(problemId: String): EditorSession?
    
    @Query("SELECT * FROM editor_sessions WHERE problemId = :problemId")
    fun getSessionFlow(problemId: String): Flow<EditorSession?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: EditorSession)
    
    @Delete
    suspend fun deleteSession(session: EditorSession)
    
    @Query("SELECT * FROM editor_sessions ORDER BY lastModified DESC LIMIT 10")
    fun getRecentSessions(): Flow<List<EditorSession>>
}

@Dao
interface CustomTestCaseDao {
    @Query("SELECT * FROM custom_test_cases WHERE problemId = :problemId ORDER BY createdAt DESC")
    fun getTestCases(problemId: String): Flow<List<CustomTestCase>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestCase(testCase: CustomTestCase)
    
    @Delete
    suspend fun deleteTestCase(testCase: CustomTestCase)
    
    @Query("DELETE FROM custom_test_cases WHERE problemId = :problemId")
    suspend fun deleteAllForProblem(problemId: String)
}
