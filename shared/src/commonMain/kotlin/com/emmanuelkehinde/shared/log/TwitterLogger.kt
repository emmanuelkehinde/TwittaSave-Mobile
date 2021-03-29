package com.emmanuelkehinde.shared.log

import io.ktor.client.features.logging.*

interface TwitterLogger: Logger {
    override fun log(message: String)
}