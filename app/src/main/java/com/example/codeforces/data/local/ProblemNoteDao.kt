package com.example.codeforces.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProblemNoteDao {
    @Query("SELECT * FROM problem_notes WHERE problemId = :problemId")
    fun getNote(problemId: String): Flow<ProblemNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: ProblemNote)

    @Query("DELETE FROM problem_notes WHERE problemId = :problemId")
    suspend fun deleteNote(problemId: String)
}
