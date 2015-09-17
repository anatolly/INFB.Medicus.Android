package com.intrafab.medicus.medJournal.loaders;

import android.content.Context;
import android.os.AsyncTask;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Анна on 27.08.2015.
 */
    public class PeriodCalendarEntrySaver extends AsyncTask {

        public static final String TAG = PeriodCalendarEntrySaver.class.getName();

        private PeriodCalendarEntry[] mData;

    Context mContext;

    public PeriodCalendarEntrySaver (Context context, HashMap<Long, PeriodCalendarEntry> entries){
        mContext = context;
        PeriodCalendarEntry[] data = new PeriodCalendarEntry[entries.size()];
        int i = 0;
        for(PeriodCalendarEntry entry : entries.values())
            data[i++] = entry;
        mData = data;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        DBManager.getInstance().insertArrayObject(mContext, Constants.Prefs.PREF_PARAM_PERIOD_CAL_ENTRIES, mData);
        return null;
    }
}
