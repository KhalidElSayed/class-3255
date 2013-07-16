
package com.twitter.android.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusUpdateService extends IntentService {
    private static final String TAG = StatusUpdateService.class.getSimpleName();

    private YambaClient yambaClient;

    public StatusUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.yambaClient = new YambaClient("student", "password");
        Log.d(TAG, "onCreate()'d");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // runs in a background thread!!!
        String status = intent.getStringExtra("status");
        Log.d(TAG, "Posting status of " + status.length() + " chars");
        try {
            long t = SystemClock.uptimeMillis();
            yambaClient.postStatus(status);
            t = SystemClock.uptimeMillis() - t;
            Log.d(TAG, "Posted status");
            String notification = this.getString(R.string.status_update_success, t / 1000.00);
            Toast.makeText(this, notification, Toast.LENGTH_SHORT).show();
        } catch (YambaClientException e) {
            Log.wtf(TAG, "Failed to post status", e);
            // TODO: what should we do here???
            Toast.makeText(this, R.string.status_update_failure, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()'d");
    }

}
