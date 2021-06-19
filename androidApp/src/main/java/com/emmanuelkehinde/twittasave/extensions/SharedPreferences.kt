package com.emmanuelkehinde.twittasave.extensions

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val APP_THEME = "APP_THEME"

var SharedPreferences.selectedTheme: Int
    get() {
        return getInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    set(value) {
        edit().putInt(APP_THEME, value).apply()
    }
