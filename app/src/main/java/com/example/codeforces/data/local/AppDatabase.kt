package com.example.codeforces.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EditorSession::class,
        CustomTestCase::class,
        BookmarkedProblem::class,
        ProblemNote::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun editorSessionDao(): EditorSessionDao
    abstract fun customTestCaseDao(): CustomTestCaseDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun problemNoteDao(): ProblemNoteDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codeforces_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
