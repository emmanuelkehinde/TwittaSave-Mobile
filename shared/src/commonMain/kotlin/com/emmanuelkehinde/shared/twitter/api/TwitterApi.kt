package com.emmanuelkehinde.shared.twitter.api

import com.emmanuelkehinde.shared.log.TwitterApiLogger
import com.emmanuelkehinde.shared.twitter.api.auth.TwitterAuth
import com.emmanuelkehinde.shared.twitter.exception.InvalidTweetUrlException
import com.emmanuelkehinde.shared.twitter.exception.UnknownException
import com.emmanuelkehinde.shared.twitter.model.ApiErrorResponse
import com.emmanuelkehinde.shared.twitter.model.Tweet
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.serialization.decodeFromString

class TwitterApi(private val twitterAuth: TwitterAuth, private val apiLogger: TwitterApiLogger) {

    @OptIn(InternalAPI::class)
    private val encodedBasicAuthData: String
        get() = "${twitterAuth.consumerKey}:${twitterAuth.consumerSecret}".encodeBase64()

    private val httpClient: HttpClient
        get() = HttpClient() {
            defaultRequest {
                install(HttpTimeout)
                host = HOST
                header("Content-Type", "application/json")
                if (twitterAuth.bearerToken.isEmpty()) {
                    header("Authorization", "Basic $encodedBasicAuthData")
                } else {
                    header("Authorization", "Bearer ${twitterAuth.bearerToken}")
                }
                timeout { requestTimeoutMillis = REQUEST_TIMEOUT }
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = apiLogger
                level = LogLevel.ALL
            }
            addDefaultResponseValidation()
            HttpResponseValidator {
                handleResponseException(this@TwitterApi::handleResponseException)
            }
        }

    private suspend fun handleResponseException(throwable: Throwable) {
        if (throwable !is ClientRequestException) throw UnknownException()

        runCatching {
            val textContent = throwable.response.content.readRemaining().readText()
            kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }.decodeFromString<ApiErrorResponse>(textContent)
        }.onSuccess { parsedErrorResponse ->
            val errorCode = parsedErrorResponse.errors?.firstOrNull()?.code
            if (errorCode != null && errorCode == ErrorCode.INVALID_TWEET_URL) {
                throw InvalidTweetUrlException()
            }
            throw UnknownException()
        }.onFailure {
            throw UnknownException()
        }
    }

    internal suspend fun getTweet(tweetId: String): Tweet {
        twitterAuth.fetchBearerTokenIfEmpty(httpClient)
        return fetchSingleTweet(tweetId)
    }

    private suspend fun fetchSingleTweet(tweetId: String): Tweet {
        return httpClient.get {
            url {
                protocol = URLProtocol.HTTPS
                path(RequestPath.STATUS)
                arrayOf(
                        Pair("id", tweetId),
                        Pair("tweet_mode", "extended")
                ).forEach { pair ->
                    parameters.append(pair.first, pair.second)
                }
            }
        }
    }

    private companion object {
        const val HOST = "api.twitter.com"
        const val REQUEST_TIMEOUT = 10_000L

        object RequestPath {
            const val STATUS = "1.1/statuses/show.json"
        }

        object ErrorCode {
            const val INVALID_TWEET_URL = 144
        }
    }
}
