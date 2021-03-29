//
//  TwitterCredentialsDataProvider.swift
//  TwittaSave
//
//  Created by Emmanuel Kehinde on 15/03/2021.
//

import Foundation
import shared

class TwitterCredentialsDataProvider: TwitterCredentialsProvider {

    static let shared: TwitterCredentialsProvider = TwitterCredentialsDataProvider()
    private let infoDict: [String: Any]?

    init() {
        infoDict = Bundle.main.infoDictionary
    }

    var bearerToken: String = String()
    var consumerKey: String {
        return (infoDict?["TWITTER_CONSUMER_KEY"] as? String) ?? String()
    }
    var consumerSecret: String {
        return (infoDict?["TWITTER_CONSUMER_SECRET"] as? String) ?? String()
    }
}
