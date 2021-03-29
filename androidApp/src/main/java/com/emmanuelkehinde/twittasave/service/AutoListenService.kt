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

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.emmanuelkehinde.twittasave.R
import com.emmanuelkehinde.twittasave.activities.MainActivity
import com.emmanuelkehinde.twittasave.extensions.showToast
import com.emmanuelkehinde.twittasave.receiver.StopAutoListenReceiver
import com.emmanuelkehinde.twittasave.utils.*

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class AutoListenService : Service() {
    private var notificationManager: NotificationManager? = null
    private var mClipboard: ClipboardManager? = null
    private var clipboardListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate() {
        super.onCreate()
        mClipboard = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        createNotificationChannel()
        clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
            performClipboardCheck()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        displayNotification()
        mClipboard?.addPrimaryClipChangedListener(clipboardListener)
        showToast("TwittaSave AutoListen Enabled")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun displayNotification() {
        val openActivityIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(EXTRA_AUTO_LISTEN_SERVICE_ON, true)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val openActivityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            OPEN_ACTIVITY_REQUEST_CODE,
            openActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopAutoListenIntent = Intent(applicationContext, StopAutoListenReceiver::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val stopAutoListenPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            STOP_AUTO_LISTEN_REQUEST_CODE,
            stopAutoListenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBitmap =
            BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher)
        val notificationContentTitle = "TwittaSave AutoListen Running..."
        val notificationContentText = "Copy tweet link to start downloading video or gif"

        val notification: Notification?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actionIcon =
                Icon.createWithResource(applicationContext, R.drawable.ic_stop_black_24dp)
            val actionBuilder =
                Notification.Action.Builder(actionIcon, "STOP", stopAutoListenPendingIntent)

            notification = Notification.Builder(applicationContext, GENERAL_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(notificationBitmap)
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setContentIntent(openActivityPendingIntent)
                .setAutoCancel(true)
                .addAction(actionBuilder.build())
                .build()
            notification?.flags = Notification.FLAG_NO_CLEAR
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification = Notification.Builder(applicationContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(notificationBitmap)
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setContentIntent(openActivityPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_stop_black_24dp, "STOP", stopAutoListenPendingIntent)
                .build()
            notification?.flags = Notification.FLAG_NO_CLEAR
        } else {
            notification = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(notificationBitmap)
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setContentIntent(openActivityPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_stop_black_24dp, "STOP", stopAutoListenPendingIntent)
                .build()
        }
        notificationManager?.notify(NOTIFICATION_IDENTIFIER, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val genChannel = NotificationChannel(
                GENERAL_CHANNEL,
                resources.getString(R.string.general_channel),
                NotificationManager.IMPORTANCE_HIGH
            )

            genChannel.setShowBadge(true)
            genChannel.lightColor = Color.BLUE
            notificationManager?.createNotificationChannel(genChannel)
        }
    }

    private fun performClipboardCheck() {
        mClipboard?.let {
            if (it.hasPrimaryClip()) {
                val copiedURL = mClipboard?.primaryClip?.getItemAt(0)?.text.toString()
                val serviceHelper = ServiceHelper()
                serviceHelper.beginDownloadProcess(copiedURL, applicationContext)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        if (notificationManager != null) {
            notificationManager?.cancel(NOTIFICATION_IDENTIFIER)
        }

        if (mClipboard != null && clipboardListener != null) {
            mClipboard?.removePrimaryClipChangedListener(clipboardListener)
            showToast("TwittaSave AutoListen Disabled")
        }
    }

    companion object {
        private const val GENERAL_CHANNEL = "general_channel"
    }
}
