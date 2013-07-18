
package com.twitter.android.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TimelineActivity extends Activity {
    private static final String TAG = TimelineActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_timeline);
        Log.d(TAG, "onCreate()'d");
    }

}
