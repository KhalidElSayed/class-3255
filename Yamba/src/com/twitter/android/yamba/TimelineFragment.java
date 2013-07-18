
package com.twitter.android.yamba;

import static com.twitter.android.yambacontract.TimelineContract.Columns.CREATED_AT;
import static com.twitter.android.yambacontract.TimelineContract.Columns.MESSAGE;
import static com.twitter.android.yambacontract.TimelineContract.Columns.USER;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.twitter.android.yambacontract.TimelineContract;

public class TimelineFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private static final String TAG = TimelineFragment.class.getSimpleName();

    private static final String[] FROM_COLUMN_NAMES = {
            USER, CREATED_AT, MESSAGE
    };

    private static final int[] TO_VIEW_IDS = {
            R.id.user, R.id.created_at, R.id.message
    };

    private static final ViewBinder DEFAULT_VIEW_BINDER = new ViewBinder() {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() == R.id.created_at) {
                long createdAt = cursor.getLong(columnIndex);
                CharSequence relativeCreatedAt = DateUtils.getRelativeDateTimeString(
                        view.getContext(), createdAt, 1000, DateUtils.DAY_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL);
                ((TextView) view).setText(relativeCreatedAt);
                return true;
            } else {
                return false;
            }
        }
    };

    private SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.simpleCursorAdapter = new SimpleCursorAdapter(this.getActivity(),
                R.layout.fragment_timeline_row, null, FROM_COLUMN_NAMES, TO_VIEW_IDS,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.simpleCursorAdapter.setViewBinder(DEFAULT_VIEW_BINDER);

        super.setListAdapter(this.simpleCursorAdapter);
        super.getLoaderManager().initLoader(0, null, this);
        Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader()");
        return new CursorLoader(this.getActivity(), TimelineContract.CONTENT_URI,
                TimelineContract.SUMMARY_PROJECTION, null, null,
                TimelineContract.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished()");
        this.simpleCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        this.simpleCursorAdapter.swapCursor(null);
    }
}
