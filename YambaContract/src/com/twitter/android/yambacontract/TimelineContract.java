
package com.twitter.android.yambacontract;

import static com.twitter.android.yambacontract.TimelineContract.Columns.CREATED_AT;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TimelineContract {

    public static final String AUTHORITY = "com.twitter.android.yamba.provider";

    public static final String PATH = "timeline";

    public static final Uri CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
            + AUTHORITY + "/" + PATH);

    private static final String MINOR_TYPE = "/vnd.twitter.status";

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MINOR_TYPE;

    public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + MINOR_TYPE;

    public static final String DEFAULT_SORT_ORDER = Columns.CREATED_AT + " DESC";

    public static final String[] MAX_CREATED_AT_PROJECTION = {
        "max(" + CREATED_AT + ")"
    };

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
