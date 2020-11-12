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

package com.emmanuelkehinde.twittasave.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.emmanuelkehinde.twittasave.R
import com.emmanuelkehinde.twittasave.receivers.AutoListenService
import com.emmanuelkehinde.twittasave.utils.Constant
import com.esafirm.rxdownloader.RxDownloader
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observer

class MainActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authConfig = TwitterAuthConfig(Constant.TWITTER_KEY, Constant.TWITTER_SECRET)
        Fabric.with(this, Twitter(authConfig))
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences("com.emmanuelkehinde.twittasave", Context.MODE_PRIVATE)
        swt_autolisten?.isChecked = intent.getBooleanExtra("service_on", false)

        if (!storageAllowed()) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, Constant.PERMISSIONS_STORAGE, Constant.REQUEST_EXTERNAL_STORAGE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Fetching tweet...")
        progressDialog?.setCancelable(false)
        progressDialog?.isIndeterminate = true

        // Get intent, action and MIME type
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSharedText(intent) // Handle text being sent
            }
        }

        // Method call to handle AutoListen feature
        handleAutoListen()

        btn_download?.setOnClickListener {
            val fname: String

            val tweetUrl = txt_tweet_url.text.toString()
            // Check if the tweet url field has text containing twitter.com/...
            if (tweetUrl.isNotEmpty() && tweetUrl.contains("twitter.com/")) {

                val id = getTweetId(tweetUrl)

                // Check if filename is set. If not, set the tweet Id as the filename
                fname = if (txt_filename?.text.toString().isNotEmpty()) {
                    txt_filename?.text.toString().trim { it <= ' ' }
                } else {
                    id.toString()
                }

                // Call method to get tweet
                if (id != null) {
                    getTweet(id, fname)
                }
            } else {
                alertNoUrl()
            }
        }
    }

    private fun handleAutoListen() {
        swt_autolisten?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startAutoService()
            } else {
                stopAutoService()
            }
        }
    }

    private fun stopAutoService() {
        this.stopService(Intent(this, AutoListenService::class.java))
    }

    private fun startAutoService() {
        this.startService(Intent(this, AutoListenService::class.java))
    }

    // Method handling pasting the tweet url into the field when Sharing the tweet url from the twitter app
    private fun handleSharedText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            try {
                if (sharedText.split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                    txt_tweet_url.setText(sharedText.split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[4])
                } else {
                    txt_tweet_url?.setText(sharedText.split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                }
            } catch (e: Exception) {
                Log.d("TAG", "handleSharedText: $e")
            }
        }
    }

    private fun getTweet(id: Long?, fname: String) {
        progressDialog!!.show()

        val twitterApiClient = TwitterCore.getInstance().apiClient
        val statusesService = twitterApiClient.statusesService
        val tweetCall = statusesService.show(id, null, null, null)
        tweetCall.enqueue(
            object : Callback<Tweet>() {
                override fun failure(exception: TwitterException) {
                    progressDialog!!.hide()
                    Toast.makeText(
                        this@MainActivity,
                        "Request Failed: Check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
                        if (result.data.extendedEtities.media[0].type == "video") {
                            filename = "$filename.mp4"
                        } else {
                            filename = "$filename.gif"
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
            }
        )
    }

    private fun downloadVideo(url: String, fname: String) {
        if (fname.endsWith(".mp4")) {
            progressDialog!!.setMessage("Fetching video...")
        } else {
            progressDialog!!.setMessage("Fetching gif...")
        }

        // Check if External Storage permission js allowed
        if (!storageAllowed()) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, Constant.PERMISSIONS_STORAGE, Constant.REQUEST_EXTERNAL_STORAGE)
            progressDialog!!.hide()
            Toast.makeText(this, "Kindly grant the request and try again", Toast.LENGTH_SHORT).show()
        } else {
            RxDownloader.getInstance(this)
                .download(url, fname, "video/*") // url, filename, and mimeType
                .subscribe(
                    object : Observer<String> {
                        override fun onCompleted() {
                            Toast.makeText(this@MainActivity, "Download Complete", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                        override fun onNext(s: String) {
                        }
                    }
                )

            progressDialog!!.hide()
            Toast.makeText(this, "Download Started: Check Notification", Toast.LENGTH_LONG).show()
        }
    }

    private fun storageAllowed(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            return permission == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    private fun alertNoVideo() {
        progressDialog!!.hide()
        Toast.makeText(this, "The url entered contains no video or gif file", Toast.LENGTH_LONG).show()
    }

    private fun alertNoMedia() {
        progressDialog!!.hide()
        Toast.makeText(this, "The url entered contains no media file", Toast.LENGTH_LONG).show()
    }

    private fun alertNoUrl() {
        Toast.makeText(this@MainActivity, "Enter a correct tweet url", Toast.LENGTH_LONG).show()
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

    // Shows this only the first time
    private fun loadLikeDialog() {
        if (sharedPreferences!!.getString(Constant.FIRSTRUN, "") == "") {

            val builder = AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(R.layout.like_layout)
                .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
                .setPositiveButton("Like") { dialogInterface, i ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("http://play.google.com/store/apps/details?id=com.emmanuelkehinde.twittasave")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

            val alertDialog = builder.create()
            alertDialog.show()

            sharedPreferences!!.edit().putString(Constant.FIRSTRUN, "no").apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        if (item.itemId == R.id.web) {
            val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twittasave.net"))
            urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(urlIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}
