package com.emmanuelkehinde.shared.log

import android.util.Log
import com.emmanuelkehinde.shared.BuildConfig

actual class TwitterApiLogger : TwitterLogger {
    actual override fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("Network", message)
        }
    }
}
