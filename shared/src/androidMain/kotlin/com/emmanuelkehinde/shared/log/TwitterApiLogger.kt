package com.emmanuelkehinde.shared.log

import android.util.Log
import com.emmanuelkehinde.shared.BuildConfig
import com.emmanuelkehinde.shared.log.TwitterLogger

actual class TwitterApiLogger: TwitterLogger {
    actual override fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("Network", message)
        }
    }
}