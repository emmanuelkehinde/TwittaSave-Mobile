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

package com.emmanuelkehinde.twittasave.receivers

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import android.widget.Toast

import com.emmanuelkehinde.twittasave.R
import com.emmanuelkehinde.twittasave.activities.MainActivity
import com.emmanuelkehinde.twittasave.utils.Constant
import com.emmanuelkehinde.twittasave.utils.ServiceUtil

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class AutoListenService : Service() {
    private var context: Context? = null
    private var notificationManager: NotificationManager? = null
    private var vNotification: Notification? = null
    private var vNotification1: androidx.core.app.NotificationCompat.Builder? = null
    private var mClipboard: ClipboardManager? = null
    private var listener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        mClipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()


        listener = ClipboardManager.OnPrimaryClipChangedListener { performClipboardCheck() }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val openActivityIntent = Intent(context, MainActivity::class.java)
        openActivityIntent.putExtra("service_on", true)
        openActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val openActivityPIntent = PendingIntent.getActivity(context, Constant.AUTO_REQUEST_CODE, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val stopAutoIntent = Intent(context, StopAutoListenReceiver::class.java)
        stopAutoIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val stopAutoPIntent = PendingIntent.getBroadcast(context, Constant.REQUEST_CODE, stopAutoIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val bitmap = BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val icon = Icon.createWithResource(context, R.drawable.ic_stop_black_24dp)
            val builder = Notification.Action.Builder(icon, "STOP", stopAutoPIntent)

            vNotification = Notification.Builder(context, GENERAL_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("TwittaSave AutoListen Running...")
                    .setContentText("Copy tweet URL to start downloading video or gif")
                    .setContentIntent(openActivityPIntent)
                    .setAutoCancel(true)
                    .addAction(builder.build())
                    .build()
            vNotification!!.flags = Notification.FLAG_NO_CLEAR

            notificationManager!!.notify(Constant.NOTI_IDENTIFIER, vNotification)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vNotification = Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("TwittaSave AutoListen Running...")
                    .setContentText("Copy tweet URL to start downloading video or gif")
                    .setContentIntent(openActivityPIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_stop_black_24dp, "STOP", stopAutoPIntent)
                    .build()
            vNotification!!.flags = Notification.FLAG_NO_CLEAR

            notificationManager!!.notify(Constant.NOTI_IDENTIFIER, vNotification)
        } else {
            vNotification1 = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("TwittaSave AutoListen Running...")
                    .setContentText("Copy tweet URL to start downloading video or gif")
                    .setContentIntent(openActivityPIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_stop_black_24dp, "STOP", stopAutoPIntent)

            notificationManager!!.notify(Constant.NOTI_IDENTIFIER, vNotification1!!.build())

        }



        mClipboard!!.addPrimaryClipChangedListener(listener)
        Toast.makeText(context, "TwittaSave AutoListen Enabled", Toast.LENGTH_SHORT).show()


        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val genChannel = NotificationChannel(GENERAL_CHANNEL,
                    resources.getString(R.string.general_channel),
                    NotificationManager.IMPORTANCE_HIGH)

            genChannel.setShowBadge(true)
            genChannel.lightColor = Color.BLUE
            notificationManager!!.createNotificationChannel(genChannel)
        }
    }

    private fun performClipboardCheck() {
        if (mClipboard!!.hasPrimaryClip()) {
            val copiedURL = mClipboard!!.primaryClip!!.getItemAt(0).text.toString()
            ServiceUtil.fetchTweet(copiedURL, context!!)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()

        if (notificationManager != null) {
            notificationManager!!.cancel(Constant.NOTI_IDENTIFIER)
        }

        if (mClipboard != null && listener != null) {
            this.mClipboard!!.removePrimaryClipChangedListener(listener)
            Toast.makeText(context, "TwittaSave AutoListen Disabled", Toast.LENGTH_SHORT).show()

        }
    }

    companion object {


        private val GENERAL_CHANNEL = "general channel"
    }
}
