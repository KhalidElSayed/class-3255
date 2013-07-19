
package com.twitter.android.yamba;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TimelineActivity extends Activity implements TimelineFragment.OnStatusSelectedListener {
    private static final String TAG = TimelineActivity.class.getSimpleName();

    private View statusDetailsContainer;

    private boolean dualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_timeline);
        this.statusDetailsContainer = super.findViewById(R.id.status_details);
        this.dualPane = statusDetailsContainer != null
                && statusDetailsContainer.getVisibility() == View.VISIBLE;
        Log.d(TAG, "onCreate()'d");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                super.startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_compose:
                super.startActivity(new Intent(this, StatusUpdateActivity.class));
                return true;
            case R.id.action_settings:
                super.startActivity(new Intent(this, PrefsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStatusSelected(Uri statusUri) {
        if (this.dualPane) {
            StatusDetailsFragment statusDetailsFragment = StatusDetailsFragment.build(statusUri);
            super.getFragmentManager().beginTransaction()
                    .replace(R.id.status_details, statusDetailsFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {
            super.startActivity(new Intent(Intent.ACTION_VIEW, statusUri));
        }
    }

}
