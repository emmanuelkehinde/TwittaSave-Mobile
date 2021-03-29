package com.emmanuelkehinde.shared.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Tweet(
    val id: String? = null,
    @SerialName("extended_entities")
    val extendedEntities: TweetEntity? = null
)

@Serializable
internal data class TweetEntity(
    val media: List<MediaEntity>? = null
)

@Serializable
internal data class MediaEntity(
    val type: MediaType? = null,
    @SerialName("video_info")
    val videoInfo: VideoInfo? = null
)

@Serializable
enum class MediaType {
    @SerialName("photo")
    PHOTO,
    @SerialName("video")
    VIDEO,
    @SerialName("animated_gif")
    ANIMATED_GIF
}

@Serializable
internal data class VideoInfo(
    val variants: List<Variant>? = null
)

@Serializable
internal data class Variant(
    val bitrate: Long? = null,
    @SerialName("content_type")
    val contentType: String? = null,
    val url: String? = null
)