package com.emmanuelkehinde.shared.twitter.exception

internal class TimeOutException: Exception("Internet seems to be lagging, please try again")
internal class NoVideoOrGifException: Exception("The tweet contains no video or gif")
internal class InvalidTweetUrlException: Exception("Invalid tweet link, please confirm that the link is correct and then try again")
internal class UnknownException(message: String = "Something went wrong, please try again"): Exception(message)