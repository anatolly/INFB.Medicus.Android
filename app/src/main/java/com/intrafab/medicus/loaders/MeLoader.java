package com.intrafab.medicus.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.db.DBManager;

/**
 * Created by Artemiy Terekhov on 08.06.2015.
 */
public class MeLoader extends AsyncTaskLoader<Account> {

    public static final String TAG = MeLoader.class.getName();

    private Account mData;

    public MeLoader(Context ctx) {
        super(ctx);
    }

    @Override
    public void onCanceled(Account data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    public void deliverResult(Account data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        Account oldData = mData;
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

        DBManager.getInstance().registerObserver(getContext(), this, MeLoader.class);

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
        DBManager.getInstance().unregisterObserver(this, MeLoader.class);
    }

    @Override
    public Account loadInBackground() {
        return DBManager.getInstance().readObject(getContext(), Constants.Prefs.PREF_PARAM_ME, Account.class);
    }

    private void releaseResources(Account data) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof MeLoader)
        {
            isEqual = this.TAG.equals( ((MeLoader) object).TAG );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.TAG.hashCode();
    }

}
