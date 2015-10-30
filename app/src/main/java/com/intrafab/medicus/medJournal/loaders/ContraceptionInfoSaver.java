package com.intrafab.medicus.medJournal.loaders;

import android.content.Context;
import android.os.AsyncTask;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;

/**
 * Created by Анна on 28.10.2015.
 */
public class ContraceptionInfoSaver extends AsyncTask {

    public static final String TAG = ContraceptionInfoSaver.class.getName();
    private ContraceptionInfo contraceptionInfo;
    Context mContext;

    public ContraceptionInfoSaver(ContraceptionInfo contraceptionInfo, Context context){
        this.contraceptionInfo = contraceptionInfo;
        mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        DBManager.getInstance().deleteObject(Constants.Prefs.PREF_PARAM_CONTRACEPTION_INFO);
        DBManager.getInstance().insertObject(mContext, Constants.Prefs.PREF_PARAM_CONTRACEPTION_INFO, contraceptionInfo, ContraceptionInfo.class);
        return null;
    }
}
