package com.kehinde.twittasave.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by kehinde on 7/5/17.
 */

public class StopAutoListenReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().stopService(new Intent(context,AutoListenService.class));
    }
}
