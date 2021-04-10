package com.emmanuelkehinde.shared.log

import platform.Foundation.NSLog

actual class TwitterApiLogger : TwitterLogger {
    actual override fun log(message: String) {
        if (Platform.isDebugBinary) {
            NSLog("%s", message)
        }
    }
}
