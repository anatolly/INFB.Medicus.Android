package com.intrafab.medicus.medJournal.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;

import java.util.List;

/**
 * Created by Анна on 24.08.2015.
 */
public class PeriodCalendarEntryListLoader extends AsyncTaskLoader<List<PeriodCalendarEntry>> {

    public static final String TAG = PeriodCalendarEntryListLoader.class.getName();

    private List<PeriodCalendarEntry> mData;

    public PeriodCalendarEntryListLoader(Context context) {
        super(context);
    }

    @Override
    public void onCanceled(List<PeriodCalendarEntry> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(List<PeriodCalendarEntry> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        List<PeriodCalendarEntry> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        DBManager.getInstance().registerObserver(getContext(), this, PeriodCalendarEntryListLoader.class);

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        DBManager.getInstance().unregisterObserver(this, PeriodCalendarEntryListLoader.class);
    }

    private void releaseResources(List<PeriodCalendarEntry> data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof PeriodCalendarEntryListLoader)
        {
            isEqual = this.TAG.equals( ((PeriodCalendarEntryListLoader) object).TAG );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.TAG.hashCode();
    }

    @Override
    public List<PeriodCalendarEntry> loadInBackground() {
        return DBManager.getInstance().readArrayToList(getContext(), Constants.Prefs.PREF_PARAM_PERIOD_CAL_ENTRIES, PeriodCalendarEntry[].class);
    }
}
