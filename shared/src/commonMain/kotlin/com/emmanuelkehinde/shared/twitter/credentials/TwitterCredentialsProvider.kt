package com.emmanuelkehinde.shared.twitter.credentials

interface TwitterCredentialsProvider {
    var bearerToken: String
    val consumerKey: String
    val consumerSecret: String
}