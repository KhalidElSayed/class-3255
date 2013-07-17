
package com.twitter.android.yamba;

import com.marakana.android.yamba.clientlib.YambaClient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {

    private static final String TAG = YambaApplication.class.getSimpleName();

    public static final boolean DEBUG = true;

    public static YambaApplication getYambaApp(Context context) {
        return (YambaApplication) context.getApplicationContext();
    }

    private YambaClient yambaClient;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()'d");
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "Preference change detected: " + key);
        this.yambaClient = null;
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
