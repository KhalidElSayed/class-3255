
package com.twitter.android.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {

    private static final String TAG = StatusActivity.class.getSimpleName();

    private Button statusButton;

    private EditText statusText;

    private TextView statusCounter;

    private int maxChars;

    private int statusCounterDefaultColor;

    private int statusCounterWarningColor;

    private int statusCounterErrorColor;
    
    private Handler handler;

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
        
        this.handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        final YambaClient yambaClient = new YambaClient("student", "password");
        final String status = this.statusText.getText().toString();
        Log.d(TAG, "Posting status of " + status.length() + " chars");
        new Thread() {
            public void run() {
                try {
                    long t = SystemClock.uptimeMillis();
                    yambaClient.postStatus(status);
                    t = SystemClock.uptimeMillis() - t;

                    final double duration = t / 1000.00;
                    Runnable runOnUiThread = new Runnable() {

                        @Override
                        public void run() {
                            StatusActivity.this.statusText.getText().clear();
                            Log.d(TAG, "Posted status");
                            String notification = StatusActivity.this.getString(
                                    R.string.status_update_success, duration);
                            Toast.makeText(StatusActivity.this, notification, Toast.LENGTH_SHORT)
                                    .show();
                        }

                    };
                    
                    StatusActivity.this.handler.post(runOnUiThread);

                } catch (YambaClientException e) {
                    Log.wtf(TAG, "Failed to post status", e);
                    Toast.makeText(StatusActivity.this, R.string.status_update_failure,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.start();

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

}
