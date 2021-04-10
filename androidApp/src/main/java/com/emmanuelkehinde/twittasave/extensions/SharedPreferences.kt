package com.emmanuelkehinde.twittasave.extensions

import android.content.SharedPreferences
import androidx.core.content.edit

const val IS_FIRST_RUN = "is_first_run"

var SharedPreferences.isFirstRun: Boolean
    get() = this.getBoolean(IS_FIRST_RUN, true)
    set(value) = this.edit { putBoolean(IS_FIRST_RUN, value) }
