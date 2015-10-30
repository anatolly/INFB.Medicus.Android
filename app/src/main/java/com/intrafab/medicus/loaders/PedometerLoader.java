package com.intrafab.medicus.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.calendar.pedometer.PedometerCalendar;
import com.intrafab.medicus.db.DBManager;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class PedometerLoader extends AsyncTaskLoader<PedometerCalendar> {

    public static final String TAG = PedometerLoader.class.getName();

    private PedometerCalendar mData;

    public PedometerLoader(Context ctx) {
        super(ctx);
    }

    @Override
    public void onCanceled(PedometerCalendar data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(PedometerCalendar data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        PedometerCalendar oldData = mData;
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

        DBManager.getInstance().registerObserver(getContext(), this, PedometerLoader.class);

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
        DBManager.getInstance().unregisterObserver(this, PedometerLoader.class);
    }

    @Override
    public PedometerCalendar loadInBackground() {
        return DBManager.getInstance().readObject(getContext(), Constants.Prefs.PREF_PARAM_PEDOMETER_CALENDAR, PedometerCalendar.class);
    }

    private void releaseResources(PedometerCalendar data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PedometerLoader)) return false;

        PedometerLoader that = (PedometerLoader) o;

        return !(mData != null ? !mData.equals(that.mData) : that.mData != null);

    }

    @Override
    public int hashCode() {
        return mData != null ? mData.hashCode() : 0;
    }


//    @Override
//    public boolean equals(Object object)
//    {
//        boolean isEqual= false;
//
//        if (object != null && object instanceof MeLoader)
//        {
//            isEqual = this.TAG.equals( ((MeLoader) object).TAG );
//        }
//
//        return isEqual;
//    }
//
//    @Override
//    public int hashCode() {
//        return this.TAG.hashCode();
//    }

}
