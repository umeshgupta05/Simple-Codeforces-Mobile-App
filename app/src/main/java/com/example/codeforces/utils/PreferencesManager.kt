package com.example.codeforces.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "codeforces_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USERNAME = "username"
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clearUsername() {
        prefs.edit().remove(KEY_USERNAME).apply()
    }

    fun isLoggedIn(): Boolean {
        return getUsername() != null
    }
}

