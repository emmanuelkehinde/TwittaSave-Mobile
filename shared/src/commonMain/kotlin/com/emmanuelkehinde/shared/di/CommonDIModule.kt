package com.emmanuelkehinde.shared.di

import com.emmanuelkehinde.shared.log.TwitterApiLogger
import com.emmanuelkehinde.shared.twitter.util.TwitterUtil

open class CommonDIModule {

    val twitterApiLogger: TwitterApiLogger by lazy {
        TwitterApiLogger()
    }

    val twitterUtil: TwitterUtil by lazy {
        TwitterUtil()
    }

}