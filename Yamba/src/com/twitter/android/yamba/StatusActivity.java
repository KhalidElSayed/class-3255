
package com.twitter.android.yamba;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher,
        LocationListener {

    private static final String TAG = StatusActivity.class.getSimpleName();

    private Button statusButton;

    private EditText statusText;

    private TextView statusCounter;

    private int maxChars;

    private int statusCounterDefaultColor;

    private int statusCounterWarningColor;

    private int statusCounterErrorColor;

    private LocationManager locationManager;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);

        this.statusButton = (Button) super.findViewById(R.id.status_button);
        this.statusText = (EditText) super.findViewById(R.id.status_text);
        this.statusCounter = (TextView) super.findViewById(R.id.status_counter);
        this.maxChars = Integer.parseInt(super.getText(R.string.max_chars).toString());
        this.statusCounter.setText(R.string.max_chars);
        this.statusCounterDefaultColor = this.statusCounter.getCurrentTextColor();

        this.statusButton.setOnClickListener(this);
        this.statusButton.setEnabled(false);
        this.statusText.addTextChangedListener(this);

        this.statusCounterWarningColor = super.getResources().getColor(
                R.color.status_counter_warning);
        this.statusCounterErrorColor = super.getResources().getColor(R.color.status_counter_error);

        String status = super.getIntent().getStringExtra("status");
        if (status != null) {
            Log.d(TAG, "Initializing status text");
            this.statusText.setText(status);
            // what about the location?
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                super.startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_settings:
                super.startActivity(new Intent(this, PrefsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (YambaApplication.getYambaApp(this).isSendLocationEnabled()) {
            this.locationManager = (LocationManager) super.getSystemService(LOCATION_SERVICE);
            this.location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100,
                    this);
        } else {
            this.locationManager = null;
            this.location = null;
        }
        Log.d(TAG, "onResume()'d");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.locationManager != null) {
            this.locationManager.removeUpdates(this);
        }
        Log.d(TAG, "onPause()'d");
    }

    @Override
    public void onClick(View view) {
        if (YambaApplication.getYambaApp(this).getYambaClient() == null) {
            Log.d(TAG, "Going to prefs...");
            super.startActivity(new Intent(this, PrefsActivity.class));
        } else {
            String status = this.statusText.getText().toString();
            Log.d(TAG, "Submitting request to post status via StatusUpdateService");
            Intent intent = new Intent(this, StatusUpdateService.class);
            intent.putExtra("status", status);
            intent.putExtra("location", location);
            super.startService(intent);
            this.statusText.getText().clear();
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        int length = this.maxChars - this.statusText.length();
        if (length < 0) {
            this.statusCounter.setTextColor(this.statusCounterErrorColor);
        } else if (length < 10) {
            this.statusCounter.setTextColor(this.statusCounterWarningColor);
        } else {
            this.statusCounter.setTextColor(this.statusCounterDefaultColor);
        }
        this.statusButton.setEnabled(0 <= length && length < maxChars);
        this.statusCounter.setText(String.valueOf(length));
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()");
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
