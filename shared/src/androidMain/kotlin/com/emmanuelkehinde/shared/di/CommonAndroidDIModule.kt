package com.emmanuelkehinde.shared.di

import com.emmanuelkehinde.shared.coroutines.AndroidCoroutineContextProvider
import com.emmanuelkehinde.shared.twitter.TwitterClient
import com.emmanuelkehinde.shared.twitter.api.TwitterApi
import com.emmanuelkehinde.shared.twitter.api.auth.TwitterAuth
import com.emmanuelkehinde.shared.twitter.api.auth.TwitterOAuth
import com.emmanuelkehinde.shared.twitter.credentials.TwitterCredentialsProvider

class CommonAndroidDIModule(private val twitterCredentialsProvider: TwitterCredentialsProvider) :
        CommonDIModule() {

    private val twitterAuth: TwitterAuth by lazy {
        TwitterOAuth(twitterCredentialsProvider)
    }

    private val twitterApi: TwitterApi by lazy {
        TwitterApi(twitterAuth, twitterApiLogger)
    }

    val twitterClient: TwitterClient by lazy {
        TwitterClient(twitterApi, twitterUtil, AndroidCoroutineContextProvider())
    }
}
