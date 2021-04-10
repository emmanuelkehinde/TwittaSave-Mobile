package com.emmanuelkehinde.shared.twitter.util

import com.emmanuelkehinde.shared.twitter.exception.InvalidTweetUrlException
import com.emmanuelkehinde.shared.twitter.exception.NoVideoOrGifException
import com.emmanuelkehinde.shared.twitter.exception.UnknownException
import com.emmanuelkehinde.shared.twitter.model.MediaData
import com.emmanuelkehinde.shared.twitter.model.MediaType
import com.emmanuelkehinde.shared.twitter.model.Tweet

class TwitterUtil {

    internal fun getTweetId(tweetUrl: String): String {
        if (!tweetUrl.contains("twitter.com/")) {
            throw InvalidTweetUrlException()
        }

        try {
            val split =
                    tweetUrl.split("\\/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return split[5].split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } catch (e: Exception) {
            throw InvalidTweetUrlException()
        }
    }

    internal fun getMediaDataFromTweet(tweet: Tweet): MediaData {
        val tweetId = tweet.id
        if (tweetId.isNullOrEmpty()) {
            throw UnknownException()
        }

        if (tweet.extendedEntities == null || tweet.extendedEntities.media.isNullOrEmpty()) {
            throw NoVideoOrGifException()
        }

        val mediaEntity = tweet.extendedEntities.media.first()
        val mediaType = mediaEntity.type
        if (mediaType == null || (mediaEntity.type != MediaType.VIDEO && mediaEntity.type != MediaType.ANIMATED_GIF)) {
            throw NoVideoOrGifException()
        }

        val videoVariants = mediaEntity.videoInfo?.variants?.filter {
            it.contentType?.contains("mp4") ?: false
        }
        val sortedVideoVariants = videoVariants?.sortedByDescending { it.bitrate }

        if (sortedVideoVariants?.isNullOrEmpty() == true) {
            throw NoVideoOrGifException()
        }

        val url = sortedVideoVariants.first().url
        if (url.isNullOrEmpty()) {
            throw NoVideoOrGifException()
        }

        return MediaData(tweetId, url, mediaType)
    }

}
