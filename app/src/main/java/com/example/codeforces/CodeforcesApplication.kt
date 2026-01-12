package com.example.codeforces

import android.app.Application
import com.example.codeforces.data.local.AppDatabase

class CodeforcesApplication : Application() {
    
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: CodeforcesApplication
            private set
    }
}
