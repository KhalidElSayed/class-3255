
package com.twitter.android.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()'d");
        
        // this will implicitly cause YambaApplication.onCreate() to run, which
        // will schedule our RefreshService to run
    }

}
