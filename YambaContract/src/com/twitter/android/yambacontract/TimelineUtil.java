
package com.twitter.android.yambacontract;

import android.content.ContentResolver;
import android.database.Cursor;

public final class TimelineUtil {
    private TimelineUtil() {

    }

    public static long getStatusMaxCreatedAt(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(TimelineContract.CONTENT_URI,
                TimelineContract.MAX_CREATED_AT_PROJECTION, null, null, null);
        try {
            return cursor.moveToFirst() ? cursor.getLong(0) : Long.MIN_VALUE;
        } finally {
            cursor.close();
        }
    }
}
