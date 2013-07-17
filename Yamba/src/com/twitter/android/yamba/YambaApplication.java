
package com.twitter.android.yamba;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {

    private static final String TAG = YambaApplication.class.getSimpleName();

    public static final boolean DEBUG = true;

    public static YambaApplication getYambaApp(Context context) {
        return (YambaApplication) context.getApplicationContext();
    }

    private YambaClient yambaClient;

    private SharedPreferences sharedPreferences;

    private PendingIntent startRefreshServicePendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        this.startRefreshServicePendingIntent = PendingIntent.getService(this, 0, new Intent(this,
                RefreshService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        this.scheduleRefreshService();
        Log.d(TAG, "onCreate()'d");
    }

    private void scheduleRefreshService() {
        long refreshInterval = this.getRefreshInterval();
        AlarmManager alarmManager = (AlarmManager) super.getSystemService(ALARM_SERVICE);
        if (refreshInterval > 0) {
            Log.d(TAG, "Scheduled refresh service to run every " + refreshInterval + " ms");
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    refreshInterval, startRefreshServicePendingIntent);
        } else {
            Log.d(TAG, "Canceling refresh service");
            alarmManager.cancel(startRefreshServicePendingIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (DEBUG)
            Log.d(TAG, "Preference change detected: " + key);
        if ("refreshInterval".equals(key)) {
            this.scheduleRefreshService();
        } else {
            this.yambaClient = null;
        }
    }

    public long getRefreshInterval() {
        return Long.parseLong(this.sharedPreferences.getString("refreshInterval", "0"));
    }

    public boolean isSendLocationEnabled() {
        return this.sharedPreferences.getBoolean("sendLocation", true);
    }

    public YambaClient getYambaClient() {
        if (this.yambaClient == null) {
            String username = sharedPreferences.getString("username", null);
            String password = sharedPreferences.getString("password", null);
            String apiUrl = sharedPreferences.getString("apiUrl", null);
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(apiUrl)) {
                this.yambaClient = null;
            } else {
                this.yambaClient = new YambaClient(username, password, apiUrl);
            }
        }
        return yambaClient;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate()'d");
    }

}
