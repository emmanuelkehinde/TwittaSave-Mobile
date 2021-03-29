package com.emmanuelkehinde.shared.twitter

import com.emmanuelkehinde.shared.coroutines.CoroutineContextProvider
import com.emmanuelkehinde.shared.twitter.api.TwitterApi
import com.emmanuelkehinde.shared.twitter.exception.TimeOutException
import com.emmanuelkehinde.shared.twitter.exception.UnknownException
import com.emmanuelkehinde.shared.twitter.model.MediaData
import com.emmanuelkehinde.shared.twitter.util.TwitterUtil
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class TwitterClient(
    private val twitterApi: TwitterApi,
    private val twitterUtil: TwitterUtil,
    private val coroutineContextProvider: CoroutineContextProvider
) {

    @OptIn(ExperimentalStdlibApi::class)
    fun getMediaData(tweetUrl: String, onSuccess: (MediaData) -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(coroutineContextProvider.io).launch {
            try {
                val tweetId = twitterUtil.getTweetId(tweetUrl)
                val tweet = twitterApi.getTweet(tweetId)
                val mediaData = twitterUtil.getMediaDataFromTweet(tweet)
                CoroutineScope(coroutineContextProvider.main).launch {
                    onSuccess(mediaData)
                }
            } catch (e: Exception) {
                CoroutineScope(coroutineContextProvider.main).launch {
                    when(e) {
                        is CancellationException -> {
                            onError(UnknownException())
                        }
                        is SocketTimeoutException -> {
                            onError(TimeOutException())
                        }
                        else -> {
                            onError(e)
                        }
                    }
                }
            }
        }
    }

}