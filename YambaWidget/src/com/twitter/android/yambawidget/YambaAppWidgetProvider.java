
package com.twitter.android.yambawidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class YambaAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.yamba_appwidget);
            remoteViews.setTextViewText(R.id.user, "John Smith");
            remoteViews.setTextViewText(R.id.created_at, "never");
            remoteViews.setTextViewText(R.id.message, "Hello from App Widget!");
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

}
