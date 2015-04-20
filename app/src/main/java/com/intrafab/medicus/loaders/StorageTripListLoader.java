package com.intrafab.medicus.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.StorageInfo;
import com.intrafab.medicus.db.DBManager;

import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageTripListLoader extends AsyncTaskLoader<List<StorageInfo>> {

    public static final String TAG = StorageTripListLoader.class.getName();

    private List<StorageInfo> mData;

    public StorageTripListLoader(Context ctx) {
        super(ctx);
    }

    @Override
    public void onCanceled(List<StorageInfo> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(List<StorageInfo> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        List<StorageInfo> oldData = mData;
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

        DBManager.getInstance().registerObserver(getContext(), this, StorageTripListLoader.class);

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
        DBManager.getInstance().unregisterObserver(this, StorageTripListLoader.class);
    }

    @Override
    public List<StorageInfo> loadInBackground() {
        return DBManager.getInstance().readArrayToList(getContext(), Constants.Prefs.PREF_PARAM_STORAGE_TRIP, StorageInfo[].class);
    }

    private void releaseResources(List<StorageInfo> data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof StorageTripListLoader)
        {
            isEqual = this.TAG.equals( ((StorageTripListLoader) object).TAG );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.TAG.hashCode();
    }

}
