package com.rsschool.myapplication.loyaltycards.data.repository

import android.content.SharedPreferences
import javax.inject.Inject

class UserPrefsRepository @Inject constructor(private val preferences: SharedPreferences)  {
    companion object {
        private const val LOGGED_IN_USER_NAME = "user_name"
    }

    var loggedInUserUid: String
        get() = preferences.getString(LOGGED_IN_USER_NAME, "") ?: ""
        set(userName) = preferences.edit().putString(LOGGED_IN_USER_NAME, userName).apply()
}