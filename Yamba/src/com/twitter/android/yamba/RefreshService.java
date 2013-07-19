
package com.twitter.android.yamba;

import static com.twitter.android.yambacontract.TimelineContract.Columns.CREATED_AT;
import static com.twitter.android.yambacontract.TimelineContract.Columns.ID;
import static com.twitter.android.yambacontract.TimelineContract.Columns.LATITUDE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.LONGITUDE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.MESSAGE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.USER;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineProcessor;
import com.marakana.android.yamba.clientlib.YambaClientException;
import com.twitter.android.yambacontract.TimelineContract;
import com.twitter.android.yambacontract.TimelineUtil;
import com.twitter.android.yambacontract.YambaContract;

public class RefreshService extends IntentService implements TimelineProcessor {
    private static final String TAG = RefreshService.class.getSimpleName();

    private ContentResolver contentResolver;

    private long maxCreatedAt;

    private int counter = 0;

    private Status lastStatus;

    private final ContentValues contentValues = new ContentValues(6);

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.contentResolver = super.getContentResolver();
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // executes on a single thread
        Log.d(TAG, "onHandleIntent()");
        YambaClient yambaClient = YambaApplication.getYambaApp(this).getYambaClient();
        if (yambaClient == null) {
            Log.w(TAG, "Ignoring request to refresh when the client is missing");
        } else {
            this.maxCreatedAt = TimelineUtil.getStatusMaxCreatedAt(contentResolver);
            try {
                yambaClient.fetchFriendsTimeline(this);
            } catch (YambaClientException e) {
                Log.wtf(TAG, "Failed to fetch timeline", e);
            }
        }
    }

    @Override
    public void onTimelineStatus(Status status) {
        long createdAt = status.getCreatedAt().getTime();
        if (createdAt > maxCreatedAt) {
            contentValues.put(ID, status.getId());
            contentValues.put(MESSAGE, status.getMessage());
            contentValues.put(CREATED_AT, createdAt);
            contentValues.put(USER, status.getUser());
            double latitude = status.getLatitude();
            double longitute = status.getLongitude();
            if (!Double.isNaN(latitude) && !Double.isNaN(longitute)) {
                contentValues.put(LATITUDE, latitude);
                contentValues.put(LONGITUDE, longitute);
            }
            if (YambaApplication.DEBUG)
                Log.d(TAG, "Storing status as " + contentValues);
            // TODO: use bulkInsert instead?
            contentResolver.insert(TimelineContract.CONTENT_URI, contentValues);
            contentValues.clear();
            this.counter++;
            this.lastStatus = status;
        } else {
            if (YambaApplication.DEBUG)
                Log.d(TAG, "Skipping existing status " + status.getId());
        }
    }

    @Override
    public void onStartProcessingTimeline() {
        // Debug.startMethodTracing(Environment.getExternalStorageDirectory().getAbsolutePath()
        // + "/refresh.trace");

    }

    @Override
    public void onEndProcessingTimeline() {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "Stored " + counter + " statuses");
//        Debug.stopMethodTracing();

        if (this.counter > 0) {
            Intent broadcast = new Intent(YambaContract.ACTION_NEW_STATUS);
            broadcast.putExtra("count", this.counter);
            Bundle lastStatusBundle = new Bundle(4);
            lastStatusBundle.putLong("id", this.lastStatus.getId());
            lastStatusBundle.putString("user", this.lastStatus.getUser());
            lastStatusBundle.putString("message", this.lastStatus.getMessage());
            lastStatusBundle.putLong("createdAt", this.lastStatus.getCreatedAt().getTime());
            lastStatusBundle.putDouble("latitude", this.lastStatus.getLatitude());
            lastStatusBundle.putDouble("longitude", this.lastStatus.getLongitude());
            broadcast.putExtra("lastStatus", lastStatusBundle);

            if (YambaApplication.DEBUG)
                Log.d(TAG, "Sending broadcast " + broadcast);
            super.sendBroadcast(broadcast, Manifest.permission.RECEIVE_NEW_STATUS);

            if (!YambaApplication.getYambaApp(this).isInTimeline()) {
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(
                        Intent.ACTION_VIEW, TimelineContract.CONTENT_URI),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle(this.getText(R.string.new_status_notification_title))
                        .setContentText(
                                this.getString(R.string.new_status_notification_text, counter))
                        .setContentIntent(pendingIntent).build();

                NotificationManager notificationManager = (NotificationManager) super
                        .getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(YambaApplication.NEW_STATUS_NOTIFICATION_ID,
                        notification);
            }
            this.lastStatus = null;
            this.counter = 0;
        }
    }

    @Override
    public boolean isRunnable() {
        return true;
    }
}
