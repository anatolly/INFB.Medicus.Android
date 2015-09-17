package com.intrafab.medicus.medJournal.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by 1 on 08.09.2015.
 */
public class PeriodCycleEntryLoader extends AsyncTaskLoader<List<PeriodCycleEntry>> {


    public static final String TAG = PeriodCycleEntryLoader.class.getName();

    private List<PeriodCycleEntry> mData;

    public PeriodCycleEntryLoader(Context context) {
        super(context);
    }

    @Override
    public void onCanceled(List<PeriodCycleEntry> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(List<PeriodCycleEntry> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        List<PeriodCycleEntry> oldData = mData;
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

        DBManager.getInstance().registerObserver(getContext(), this, PeriodCycleEntryLoader.class);

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
        DBManager.getInstance().unregisterObserver(this, PeriodCycleEntryLoader.class);
    }

    private void releaseResources(List<PeriodCycleEntry> data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof PeriodCycleEntryLoader)
        {
            isEqual = this.TAG.equals( ((PeriodCycleEntryLoader) object).TAG );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.TAG.hashCode();
    }

    @Override
    public List<PeriodCycleEntry> loadInBackground() {
        return DBManager.getInstance().readArrayToList(getContext(), Constants.Prefs.PREF_PARAM_PERIOD_CYCLE_ENTRIES, PeriodCycleEntry[].class,
                new Comparator<PeriodCycleEntry>() {
            @Override
            public int compare(PeriodCycleEntry o, PeriodCycleEntry o1) {
                Date date = new Date(o.getFirstDay());
                Date date1 = new Date(o1.getFirstDay());
                if (date == null || date1 == null)
                    return 0;
                return date.compareTo(date1);
            }
        }, false);

    }
}
