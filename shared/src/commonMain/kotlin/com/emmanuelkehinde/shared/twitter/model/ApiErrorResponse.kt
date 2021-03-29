package com.emmanuelkehinde.shared.twitter.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiErrorResponse(
    val errors: List<Error>? = null
)

@Serializable
internal data class Error(
    val code: Int? = null,
    val message: String? = null
)
