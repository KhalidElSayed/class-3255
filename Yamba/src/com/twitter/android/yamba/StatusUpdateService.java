
package com.twitter.android.yamba;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusUpdateService extends IntentService {
    private static final String TAG = StatusUpdateService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 0;

    public StatusUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()'d");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // runs in a background thread!!!
        String status = intent.getStringExtra("status");
        Location location = intent.getParcelableExtra("location");
        Log.d(TAG, "Posting status of " + status.length() + " chars from " + location);

        CharSequence tickerText = this.getText(R.string.status_update_posting_text);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(this.getText(R.string.status_update_posting_title))
                .setContentText(tickerText).setTicker(tickerText).build();

        NotificationManager notificationManager = (NotificationManager) super
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

        try {
            long t = SystemClock.uptimeMillis();
            YambaClient yambaClient = YambaApplication.getYambaApp(this).getYambaClient();
            if (yambaClient == null) {
                throw new YambaClientException("No client");
            }
            if (location == null) {
                yambaClient.postStatus(status);
            } else {
                yambaClient.postStatus(status, location.getLatitude(), location.getLongitude());
            }
            t = SystemClock.uptimeMillis() - t;
            Log.d(TAG, "Posted status in " + t + " ms");
            notificationManager.cancel(NOTIFICATION_ID);
        } catch (YambaClientException e) {
            Log.wtf(TAG, "Failed to post status", e);
            tickerText = this.getText(R.string.status_update_failure);

            Intent recoveryIntent = new Intent(this, StatusActivity.class);
            recoveryIntent.putExtra("status", status);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, recoveryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new Notification.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentTitle(this.getText(R.string.status_update_posting_title))
                    .setContentText(tickerText).setTicker(tickerText)
                    .setContentIntent(pendingIntent).build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()'d");
    }

}
