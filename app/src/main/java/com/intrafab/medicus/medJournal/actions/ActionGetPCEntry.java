package com.intrafab.medicus.medJournal.actions;

import android.os.Bundle;

import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;
import com.intrafab.medicus.actions.ActionRequestStorageTask;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.medJournal.loaders.PeriodCalendarEntryListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.List;

/**
 * Created by 1 on 02.09.2015.
 */
public class ActionGetPCEntry extends GroundyTask {

    public static final String TAG = ActionRequestStorageTask.class.getName();

    public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

    @Override
    protected TaskResult doInBackground() {

        Bundle inputBundle = getArgs();
        String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

        if (!Connectivity.isNetworkConnected()) {
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        try {
            HttpRestService service = RestApiConfig.getMedicusRestService();
            List<PeriodCalendarEntry> entryList = service.getEntry(AppApplication.getToken(), AppApplication.getSessName() + "=" + AppApplication.getSessId(), userUid);

            if (entryList.size() > 0)
                DBManager.getInstance().insertArrayObject(getContext(), PeriodCalendarEntryListLoader.class, Constants.Prefs.PREF_PARAM_PERIOD_CAL_ENTRIES, entryList, PeriodCalendarEntry.class);

        } catch (Exception e) {
            e.printStackTrace();
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return succeeded();
    }








}



