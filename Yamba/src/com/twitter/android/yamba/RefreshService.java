
package com.twitter.android.yamba;

import static com.twitter.android.yambacontract.TimelineContract.Columns.CREATED_AT;
import static com.twitter.android.yambacontract.TimelineContract.Columns.ID;
import static com.twitter.android.yambacontract.TimelineContract.Columns.LATITUDE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.LONGITUDE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.MESSAGE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.USER;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineProcessor;
import com.marakana.android.yamba.clientlib.YambaClientException;
import com.twitter.android.yambacontract.TimelineContract;
import com.twitter.android.yambacontract.TimelineUtil;

public class RefreshService extends IntentService implements TimelineProcessor {
    private static final String TAG = RefreshService.class.getSimpleName();

    private ContentResolver contentResolver;

    private long maxCreatedAt;

    private int counter = 0;

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
        } else {
            if (YambaApplication.DEBUG)
                Log.d(TAG, "Skipping existing status " + status.getId());
        }
    }

    @Override
    public void onStartProcessingTimeline() {
        this.counter = 0;
    }

    @Override
    public void onEndProcessingTimeline() {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "Stored " + counter + " statuses");
    }

    @Override
    public boolean isRunnable() {
        return true;
    }
}
