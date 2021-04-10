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

package com.emmanuelkehinde.twittasave.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.emmanuelkehinde.shared.di.CommonAndroidDIModule
import com.emmanuelkehinde.shared.twitter.model.MediaData
import com.emmanuelkehinde.shared.twitter.model.MediaType
import com.emmanuelkehinde.twittasave.data.TwitterCredentialsDataProvider
import com.emmanuelkehinde.twittasave.extensions.showLongToast
import com.emmanuelkehinde.twittasave.extensions.showToast
import com.esafirm.rxdownloader.RxDownloader
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by kehinde on 7/5/17.
 */

class ServiceHelper {

    fun beginDownloadProcess(tweetUrl: String, context: Context) {
        val commonDIModule = CommonAndroidDIModule(TwitterCredentialsDataProvider)
        val twitterClient = commonDIModule.twitterClient
        twitterClient.getMediaData(
            tweetUrl = tweetUrl,
            onSuccess = { mediaData ->
                val fileName = getFileName(mediaData)
                downloadVideo(mediaData.downloadLink, fileName, context)
            },
            onError = { throwable ->
                context.showLongToast(throwable.message)
            }
        )
    }

    private fun getFileName(mediaData: MediaData): String {
        val fileName = mediaData.tweetId
        return if (mediaData.mediaType == MediaType.VIDEO) {
            "$fileName.mp4"
        } else {
            "$fileName.gif"
        }
    }

    private fun downloadVideo(url: String, fileName: String, context: Context) {
        // Check if External Storage permission is allowed
        if (!isStoragePermissionGranted(context)) {
            // We don't have permission so prompt the user
            context.showLongToast("Kindly grant the storage permission for TwittaSave and try again")
            return
        }

        RxDownloader(context)
            .download(url, fileName, "video/*", true) // url, filename, and mimeType
            .subscribe(
                object : Observer<String> {
                    override fun onComplete() {
                        context.showToast("Download Complete")
                    }

                    override fun onError(e: Throwable) {
                        context.showLongToast(e.localizedMessage)
                    }

                    override fun onNext(s: String) {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }
                }
            )

        context.showLongToast("Download Started: Check Notification")
    }

    private fun isStoragePermissionGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return permission == PackageManager.PERMISSION_GRANTED
        }

        return true
    }
}
