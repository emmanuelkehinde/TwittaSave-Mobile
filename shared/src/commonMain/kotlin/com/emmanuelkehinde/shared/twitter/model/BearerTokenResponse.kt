package com.emmanuelkehinde.shared.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BearerTokenResponse(
        @SerialName("token_type")
        val tokenType: String,
        @SerialName("access_token")
        val accessToken: String
)
