package com.intrafab.medicus.medJournal.loaders;

import android.content.Context;
import android.os.AsyncTask;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.medJournal.data.PeriodCycleEntry;

import java.util.ArrayList;

/**
 * Created by 1 on 08.09.2015.
 */
public class PeriodCycleEntrySaver  extends AsyncTask {

    public static final String TAG = PeriodCalendarEntrySaver.class.getName();

    private PeriodCycleEntry[] mData;

    Context mContext;

    public PeriodCycleEntrySaver (Context context, ArrayList<PeriodCycleEntry> entries){
        mContext = context;
        PeriodCycleEntry[] data = new PeriodCycleEntry[entries.size()];
        int i = 0;
        for(PeriodCycleEntry entry : entries)
            data[i++] = entry;
        mData = data;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        DBManager.getInstance().insertArrayObject(mContext, Constants.Prefs.PREF_PARAM_PERIOD_CYCLE_ENTRIES, mData);
        return null;
    }
}
