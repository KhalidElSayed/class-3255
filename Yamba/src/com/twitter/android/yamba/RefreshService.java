
package com.twitter.android.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineProcessor;
import com.marakana.android.yamba.clientlib.YambaClientException;

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
            try {
                yambaClient.fetchFriendsTimeline(new TimelineProcessor() {

                    @Override
                    public void onTimelineStatus(Status status) {
                        if (YambaApplication.DEBUG)
                            Log.d(TAG, "Got " + status);
                        // save to db

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
