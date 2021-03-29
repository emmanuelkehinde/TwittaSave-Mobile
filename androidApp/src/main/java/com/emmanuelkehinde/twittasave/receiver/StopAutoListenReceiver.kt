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

package com.emmanuelkehinde.twittasave.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.emmanuelkehinde.twittasave.service.AutoListenService

/**
 * Created by kehinde on 7/5/17.
 */

class StopAutoListenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.applicationContext.stopService(Intent(context, AutoListenService::class.java))
    }
}
