package com.intrafab.medicus.medJournal.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;

import java.util.List;

/**
 * Created by Анна on 28.10.2015.
 */
public class ContraceptionInfoLoader extends AsyncTaskLoader<ContraceptionInfo>  {

    public static final String TAG = ContraceptionInfoLoader.class.getName();

    private ContraceptionInfo contraceptionInfo;

    public ContraceptionInfoLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(ContraceptionInfo data) {
        if (isReset()) {
            return;
        }
        contraceptionInfo = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (contraceptionInfo != null) {
            deliverResult(contraceptionInfo);
        }

        DBManager.getInstance().registerObserver(getContext(), this, ContraceptionInfoLoader.class);

        if (takeContentChanged() || contraceptionInfo == null) {
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
        contraceptionInfo = null;

        // The Loader is being reset, so we should stop monitoring for changes.
        DBManager.getInstance().unregisterObserver(this, ContraceptionInfoLoader.class);
    }


    @Override
    public ContraceptionInfo loadInBackground() {
        return DBManager.getInstance().readObject(getContext(), Constants.Prefs.PREF_PARAM_CONTRACEPTION_INFO, ContraceptionInfo.class);
    }
}
