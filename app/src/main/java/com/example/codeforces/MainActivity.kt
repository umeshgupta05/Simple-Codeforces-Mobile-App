package com.example.codeforces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.codeforces.ui.theme.CodeforcesTheme
import com.example.codeforces.ui.navigation.CodeforcesApp
import com.example.codeforces.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val preferencesManager = PreferencesManager(this)
        
        setContent {
            CodeforcesTheme {
                CodeforcesApp(preferencesManager = preferencesManager)
                }
            }
    }
}