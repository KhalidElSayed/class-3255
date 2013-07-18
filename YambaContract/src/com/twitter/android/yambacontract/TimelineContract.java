
package com.twitter.android.yambacontract;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TimelineContract {

    public static final String AUTHORITY = "com.twitter.android.yamba.provider";

    public static final String PATH = "timeline";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    private static final String MINOR_TYPE = "vnd.twitter.status";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + MINOR_TYPE;

    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + MINOR_TYPE;

    public static final String DEFAULT_SORT_ORDER = Columns.CREATED_AT + " DESC";

    public static final class Columns {

        public static final String ID = BaseColumns._ID;

        public static final String MESSAGE = "message";

        public static final String CREATED_AT = "createdAt";

        public static final String USER = "user";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        private Columns() {

        }
    }

    private TimelineContract() {

    }
}
