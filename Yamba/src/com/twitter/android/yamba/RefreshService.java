
package com.twitter.android.yamba;

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

import static com.twitter.android.yambacontract.TimelineContract.Columns.*;

public class RefreshService extends IntentService {
    private static final String TAG = RefreshService.class.getSimpleName();

    public RefreshService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        YambaClient yambaClient = YambaApplication.getYambaApp(this).getYambaClient();
        if (yambaClient == null) {
            Log.w(TAG, "Ignoring request to refresh when the client is missing");
        } else {
            final ContentResolver contentResolver = super.getContentResolver();
            try {
                yambaClient.fetchFriendsTimeline(new TimelineProcessor() {

                    private ContentValues contentValues = new ContentValues();

                    @Override
                    public void onTimelineStatus(Status status) {
                        if (YambaApplication.DEBUG)
                            Log.d(TAG, "Got " + status);
                        contentValues.clear();
                        contentValues.put(ID, status.getId());
                        contentValues.put(MESSAGE, status.getMessage());
                        contentValues.put(CREATED_AT, status.getCreatedAt().getTime());
                        contentValues.put(USER, status.getUser());
                        double latitude = status.getLatitude();
                        double longitute = status.getLongitude();
                        if (!Double.isNaN(latitude) && !Double.isNaN(longitute)) {
                            contentValues.put(LATITUDE, latitude);
                            contentValues.put(LONGITUDE, longitute);
                        }
                        contentResolver.insert(TimelineContract.CONTENT_URI, contentValues);

                    }

                    @Override
                    public void onStartProcessingTimeline() {
                    }

                    @Override
                    public void onEndProcessingTimeline() {
                    }

                    @Override
                    public boolean isRunnable() {
                        return true;
                    }
                });
            } catch (YambaClientException e) {
                Log.wtf(TAG, "Failed to fetch timeline", e);
            }
        }
    }
}
