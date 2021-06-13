package com.emmanuelkehinde.twittasave

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.emmanuelkehinde.twittasave.extensions.selectedTheme

class TwittaSaveApplication : Application() {

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(sharedPreferences.selectedTheme)
    }
}
