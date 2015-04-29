package com.intrafab.medicus.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.db.DBManager;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class StateEntryListLoader extends AsyncTaskLoader<List<StateEntry>> {

    public static final String TAG = StateEntryListLoader.class.getName();

    private List<StateEntry> mData;

    public StateEntryListLoader(Context ctx) {
        super(ctx);
    }

    @Override
    public void onCanceled(List<StateEntry> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(List<StateEntry> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        List<StateEntry> oldData = mData;
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

        DBManager.getInstance().registerObserver(getContext(), this, StateEntryListLoader.class);

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
        DBManager.getInstance().unregisterObserver(this, StateEntryListLoader.class);
    }

    @Override
    public List<StateEntry> loadInBackground() {
        return DBManager.getInstance().readArrayToList(getContext(),
                Constants.Prefs.PREF_PARAM_STTATE_ENTRIES,
                StateEntry[].class,
                new Comparator<StateEntry>() {
                    @Override
                    public int compare(StateEntry o, StateEntry o1) {
                        Date date = new Date(o.getStateStart());
                        Date date1 = new Date(o1.getStateStart());
                        if (date == null || date1 == null)
                            return 0;
                        return date.compareTo(date1);
                    }
                }, false);
    }

    private void releaseResources(List<StateEntry> data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof StateEntryListLoader)
        {
            isEqual = this.TAG.equals( ((StateEntryListLoader) object).TAG );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.TAG.hashCode();
    }

}
