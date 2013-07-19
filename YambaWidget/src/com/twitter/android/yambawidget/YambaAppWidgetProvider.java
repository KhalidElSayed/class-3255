
package com.twitter.android.yambawidget;

import static com.twitter.android.yambacontract.TimelineContract.Columns.CREATED_AT;
import static com.twitter.android.yambacontract.TimelineContract.Columns.ID;
import static com.twitter.android.yambacontract.TimelineContract.Columns.MESSAGE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.USER;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.twitter.android.yambacontract.TimelineContract;
import com.twitter.android.yambacontract.TimelineUtil;

public class YambaAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = YambaAppWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate(...)");
        PendingIntent startTimelineActivity = PendingIntent.getActivity(context, 0, new Intent(
                Intent.ACTION_VIEW, TimelineContract.CONTENT_URI),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent startStatusDetailsActivity = null;
        ContentResolver contentResolver = context.getContentResolver();
        long id = 0;
        long createdAt = TimelineUtil.getStatusMaxCreatedAt(contentResolver);
        String user = null;
        String message = null;
        if (createdAt > 0) {
            Cursor c = contentResolver.query(TimelineContract.CONTENT_URI,
                    TimelineContract.SUMMARY_PROJECTION, CREATED_AT + "=" + createdAt, null, null);
            try {
                if (c.moveToFirst()) {
                    id = c.getLong(c.getColumnIndex(ID));
                    user = c.getString(c.getColumnIndex(USER));
                    message = c.getString(c.getColumnIndex(MESSAGE));
                    startStatusDetailsActivity = PendingIntent.getActivity(
                            context,
                            0,
                            new Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(
                                    TimelineContract.CONTENT_URI, id)),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.d(TAG, "Got status " + id + ". Set up action: "
                            + startStatusDetailsActivity);
                }
            } finally {
                c.close();
            }
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.yamba_appwidget);
            remoteViews.setOnClickPendingIntent(R.id.image_button, startTimelineActivity);
            remoteViews.setTextViewText(R.id.user, user);
            remoteViews.setTextViewText(R.id.created_at, DateUtils.getRelativeDateTimeString(
                    context, createdAt, DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL));
            remoteViews.setTextViewText(R.id.message, message);
            remoteViews.setOnClickPendingIntent(R.id.user, startStatusDetailsActivity);
            remoteViews.setOnClickPendingIntent(R.id.created_at, startStatusDetailsActivity);
            remoteViews.setOnClickPendingIntent(R.id.message, startStatusDetailsActivity);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
