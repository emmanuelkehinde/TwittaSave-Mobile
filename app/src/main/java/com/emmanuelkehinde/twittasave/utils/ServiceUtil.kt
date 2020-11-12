/*
 * Copyright (C) 2017 Emmanuel Kehinde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emmanuelkehinde.twittasave.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.esafirm.rxdownloader.RxDownloader
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import rx.Observer

/**
 * Created by kehinde on 7/5/17.
 */

object ServiceUtil {

    private var c: Context? = null

    fun fetchTweet(copiedURL: String, context: Context) {
        c = context
        val fname: String

        // Check if the tweet url field has text containing twitter.com/...
        if (copiedURL.length > 0 && copiedURL.contains("twitter.com/")) {

            val id = getTweetId(copiedURL)

            // Call method to get tweet
            if (id != null) {
                // set the tweet Id as the filename
                fname = id.toString()

                getTweet(id, fname)
            }
        } else {
            alertNoUrl()
        }
    }

    private fun getTweetId(s: String): Long? {
        return try {
            val split = s.split("\\/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = split[5].split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            java.lang.Long.parseLong(id)
        } catch (e: Exception) {
            Log.d("TAG", "getTweetId: " + e.localizedMessage!!)
            alertNoUrl()
            null
        }
    }

    private fun alertNoVideo() {
        Toast.makeText(c, "The tweet contains no video or gif file", Toast.LENGTH_LONG).show()
    }

    private fun alertNoMedia() {
        Toast.makeText(c, "The tweet contains no media file", Toast.LENGTH_LONG).show()
    }

    private fun alertNoUrl() {
        Toast.makeText(c, "Not a tweet url", Toast.LENGTH_LONG).show()
    }

    private fun showFetchingTweetNoti() {
        Toast.makeText(c, "Fetching Tweet...", Toast.LENGTH_LONG).show()
    }

    private fun getTweet(id: Long?, fname: String) {
        showFetchingTweetNoti()

        val twitterApiClient = TwitterCore.getInstance().apiClient
        val statusesService = twitterApiClient.statusesService
        val tweetCall = statusesService.show(id, null, null, null)
        tweetCall.enqueue(
            object : Callback<Tweet>() {
                override fun success(result: Result<Tweet>) {

                    // Check if media is present
                    if (result.data.extendedEtities == null && result.data.entities.media == null) {
                        alertNoMedia()
                    } else if (result.data.extendedEtities.media[0].type != "video" && result.data.extendedEtities.media[0].type != "animated_gif") {
                        alertNoVideo()
                    } else {
                        var filename = fname
                        var url: String

                        // Set filename to gif or mp4
                        filename = if (result.data.extendedEtities.media[0].type == "video") {
                            "$filename.mp4"
                        } else {
                            "$filename.gif"
                        }

                        var i = 0
                        url = result.data.extendedEtities.media[0].videoInfo.variants[i].url
                        while (!url.contains(".mp4")) {
                            try {
                                if (result.data.extendedEtities.media[0].videoInfo.variants[i] != null) {
                                    url = result.data.extendedEtities.media[0].videoInfo.variants[i].url
                                    i += 1
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                downloadVideo(url, filename)
                            }
                        }

                        downloadVideo(url, filename)
                    } // Check if gif or mp4 present in the file
                }

                override fun failure(exception: TwitterException) {
                    Toast.makeText(c, "Request Failed: Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun downloadVideo(url: String, fname: String) {
        // Check if External Storage permission js allowed
        if (!storageAllowed()) {
            // We don't have permission so prompt the user
            // ActivityCompat.requestPermissions(c, Constant.PERMISSIONS_STORAGE, Constant.REQUEST_EXTERNAL_STORAGE);
            Toast.makeText(c, "Kindly grant the storage permission for TwittaSave and try again", Toast.LENGTH_SHORT).show()
        } else {
            RxDownloader.getInstance(c)
                .download(url, fname, "video/*") // url, filename, and mimeType
                .subscribe(
                    object : Observer<String> {
                        override fun onCompleted() {
                            Toast.makeText(c, "Download Completed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(c, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                        override fun onNext(s: String) {
                        }
                    }
                )

            Toast.makeText(
                c,
                "Download Started: Check Notification",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun storageAllowed(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ActivityCompat.checkSelfPermission(c!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            return permission == PackageManager.PERMISSION_GRANTED
        }

        return true
    }
}
