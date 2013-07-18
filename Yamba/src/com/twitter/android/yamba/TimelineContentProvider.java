
package com.twitter.android.yamba;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.twitter.android.yambacontract.TimelineContract;

public class TimelineContentProvider extends ContentProvider {

    private static final String TAG = TimelineContentProvider.class.getSimpleName();

    private TimelineDb timelineDb;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int STATUS_DIR = 1;

    private static final int STATUS_ITEM = 2;
    static {
        URI_MATCHER.addURI(TimelineContract.AUTHORITY, TimelineContract.PATH, STATUS_DIR);
        URI_MATCHER.addURI(TimelineContract.AUTHORITY, TimelineContract.PATH + "/#", STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()'d");
        this.timelineDb = new TimelineDb(super.getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "getType(" + uri + ")");
        switch (URI_MATCHER.match(uri)) {
            case STATUS_DIR:
                return TimelineContract.CONTENT_TYPE;
            case STATUS_ITEM:
                return TimelineContract.CONTENT_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "query(" + uri + "," + Arrays.toString(projection) + "," + selection + ","
                    + Arrays.toString(selectionArgs) + "," + sortOrder + ")");
        SQLiteDatabase db = this.timelineDb.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TimelineDb.TABLE);
        switch (URI_MATCHER.match(uri)) {
            case STATUS_DIR:
                break;
            case STATUS_ITEM:
                qb.appendWhere(TimelineContract.Columns.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Refusing to query " + uri);
        }
        if (sortOrder == null) {
            sortOrder = TimelineContract.DEFAULT_SORT_ORDER;
        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        if (YambaApplication.DEBUG)
            Log.d(TAG, "Returning cursor with " + cursor.getCount() + " rows");
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "insert(" + uri + "," + values + ")");

        if (URI_MATCHER.match(uri) != STATUS_DIR) {
            throw new IllegalArgumentException("Refusing to insert at " + uri);
        }

        long id = this.timelineDb.getWritableDatabase().insert(TimelineDb.TABLE, null, values);
        if (id == -1) {
            Log.w(TAG, "Failed to insert " + values);
            return uri;
        } else {
            Uri resultUri = ContentUris.withAppendedId(uri, id);
            super.getContext().getContentResolver().notifyChange(uri, null);
            if (YambaApplication.DEBUG)
                Log.d(TAG, "Inserted " + resultUri);
            return resultUri;
        }
    }

    private String getWhere(Uri uri, String selection, String operation) {
        switch (URI_MATCHER.match(uri)) {
            case STATUS_DIR:
                return selection;
            case STATUS_ITEM:
                String where = TimelineContract.Columns.ID + "=" + uri.getLastPathSegment();
                if (selection != null) {
                    where += " AND (" + selection + ")";
                }
                return where;
            default:
                throw new IllegalArgumentException("Refusing to " + operation + " " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (YambaApplication.DEBUG)
            Log.d(TAG, "delete(" + uri + "," + selection + "," + Arrays.toString(selectionArgs)
                    + ")");
        SQLiteDatabase db = this.timelineDb.getWritableDatabase();
        int rows = db.delete(TimelineDb.TABLE, getWhere(uri, selection, "delete"), selectionArgs);
        if (rows > 0) {
            super.getContext().getContentResolver().notifyChange(uri, null);
        }
        if (YambaApplication.DEBUG)
            Log.d(TAG, "Deleted " + rows + " rows");
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (YambaApplication.DEBUG)
            Log.d(TAG,
                    "update(" + uri + "," + values + "," + selection + ","
                            + Arrays.toString(selectionArgs) + ")");
        SQLiteDatabase db = this.timelineDb.getWritableDatabase();
        int rows = db.update(TimelineDb.TABLE, values, getWhere(uri, selection, "update"),
                selectionArgs);
        if (rows > 0) {
            super.getContext().getContentResolver().notifyChange(uri, null);
        }
        if (YambaApplication.DEBUG)
            Log.d(TAG, "Updated " + rows + " rows");
        return rows;
    }

}
