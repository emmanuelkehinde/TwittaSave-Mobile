package com.emmanuelkehinde.shared.log

expect class TwitterApiLogger() : TwitterLogger {
    override fun log(message: String)
}