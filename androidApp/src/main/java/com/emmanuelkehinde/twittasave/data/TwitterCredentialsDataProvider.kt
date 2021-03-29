package com.emmanuelkehinde.twittasave.data

import com.emmanuelkehinde.shared.twitter.credentials.TwitterCredentialsProvider
import com.emmanuelkehinde.twittasave.BuildConfig

object TwitterCredentialsDataProvider : TwitterCredentialsProvider {
    override var bearerToken: String = String()
    override val consumerKey: String
        get() = BuildConfig.CONSUMER_KEY
    override val consumerSecret: String
        get() = BuildConfig.CONSUMER_SECRET
}
