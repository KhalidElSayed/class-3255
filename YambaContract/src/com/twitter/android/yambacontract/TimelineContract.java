
package com.twitter.android.yambacontract;

import android.provider.BaseColumns;

public final class TimelineContract {

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
