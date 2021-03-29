package com.emmanuelkehinde.shared.twitter.api.auth
import com.emmanuelkehinde.shared.twitter.credentials.TwitterCredentialsProvider
import com.emmanuelkehinde.shared.twitter.model.BearerTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*

interface TwitterAuth {
    val bearerToken: String
    val consumerKey: String
    val consumerSecret: String
    suspend fun fetchBearerTokenIfEmpty(httpClient: HttpClient)
}

internal class TwitterOAuth(private val credentialsProvider: TwitterCredentialsProvider) :
    TwitterAuth {

    override val bearerToken: String
        get() = credentialsProvider.bearerToken

    override val consumerKey: String
        get() = credentialsProvider.consumerKey

    override val consumerSecret: String
        get() = credentialsProvider.consumerSecret

    override suspend fun fetchBearerTokenIfEmpty(httpClient: HttpClient) {
        if (bearerToken.isNotEmpty()) {
            return
        }
        val response = httpClient.post<BearerTokenResponse> {
            header("Content-Type", "application/x-www-form-urlencoded")
            url {
                protocol = URLProtocol.HTTPS
                path(RequestPath.TOKEN)
            }
            body = Parameters.build {
                append("grant_type", "client_credentials")
            }.formUrlEncode()
        }
        credentialsProvider.bearerToken = response.accessToken
    }

    private companion object {

        object RequestPath {
            const val TOKEN = "oauth2/token"
        }
    }
}

