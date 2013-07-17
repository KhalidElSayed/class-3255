
package com.twitter.android.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.twitter.android.yambacontract.TimelineContract.Columns.*;

public class TimelineDb extends SQLiteOpenHelper {

    private static final String TAG = TimelineDb.class.getSimpleName();

    private static final String DB_NAME = "timeline.db";

    private static final int DB_VERSION = 1;

    private static final String TABLE = "timeline";

    public TimelineDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "Constructed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY NOT NULL, "
                        + "%s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, "
                        + "%s REAL, %s REAL)", TABLE, ID, MESSAGE, USER, CREATED_AT, LATITUDE,
                LONGITUDE);
        Log.d(TAG, "Creating schema: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading from " + oldVersion + " to " + newVersion);
        // OK to get rid of our local cache of the data
        // otherwise, consider incremental ALTER TABLE statements...
        db.execSQL("DROP TABLE " + TABLE);
        this.onCreate(db);
    }

}
