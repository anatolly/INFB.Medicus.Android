package com.intrafab.medicus.medJournal.actions;

import android.os.Bundle;

import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;
import com.intrafab.medicus.actions.ActionRequestUploadFileTask;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.medJournal.data.PeriodDataKeeper;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1 on 02.09.2015.
 */
public class ActionSaveCalendarEntry extends GroundyTask

    {
        public static final String TAG = ActionSaveCalendarEntry.class.getName();

        public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

        @Override
        protected TaskResult doInBackground() {

            Bundle inputBundle = getArgs();
            String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

            if (!Connectivity.isNetworkConnected()) {
                DBManager.getInstance().onContentChanged();
                return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
            }

            List<PeriodCalendarEntry> listServerData;
            HttpRestService service;
            HashMap <String, PeriodCalendarEntry> hashMapServerData;

            // try to get MedicusRestService
            try {
                service = RestApiConfig.getMedicusRestService();
            }catch (Exception e) {
                e.printStackTrace();
                DBManager.getInstance().onContentChanged();
                return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
            }

            //try to get data from server
            try {
                // get data from server
                listServerData = service.getEntry(AppApplication.getToken(), AppApplication.getSessName() + "=" + AppApplication.getSessId(), userUid);
                // create hash map for server data
                hashMapServerData = new HashMap<>(listServerData.size());
                for (PeriodCalendarEntry entry : listServerData) {
                    hashMapServerData.put(entry.getDateString(), entry);
                }
                // relese resources
                listServerData = null;
            } catch (Exception e){
                // if we didn't get any data
                hashMapServerData = new HashMap<>();
            }
            try {
                // get data from database
                HashMap<Long, PeriodCalendarEntry> localData = PeriodDataKeeper.getInstance().getCalendarData();
                // think that local data is more relevant that server data
                if (localData == null || localData.isEmpty())
                {
                    for (PeriodCalendarEntry serverEntry : hashMapServerData.values()) {
                        service.deletePCEntry(AppApplication.getToken(),AppApplication.getSessName() + "=" + AppApplication.getSessId(), serverEntry.getId());
                    }
                }
                else {
                    for (PeriodCalendarEntry localEntry : localData.values()) {
                        // if server data contains the same item as local
                        if (hashMapServerData.containsKey(localEntry.getDateString())) {
                            PeriodCalendarEntry serverEntry = hashMapServerData.get(localEntry.getDateString());
                            // if item is not equal, then put latest changed item to localData and to the server
                            if (!serverEntry.equals(localEntry)) {
                                localEntry.setId(serverEntry.getId());
                                service.refreshPCEntry(AppApplication.getToken(), AppApplication.getSessName() + "=" + AppApplication.getSessId(), serverEntry.getId(), localEntry);
                            } else {
                                // if item is equal
                            }
                        } else {
                            // if there is no such item in server data, put it to server
                            service.uploadPCEntry(AppApplication.getToken(), AppApplication.getSessName() + "=" + AppApplication.getSessId(), localEntry);
                        }
                    }
                    for (PeriodCalendarEntry serverEntry : hashMapServerData.values()) {
                        Logger.d (TAG, "find unnecessary item in server");
                        if (!localData.containsKey(serverEntry.getTimeInSec()))
                            service.deletePCEntry(AppApplication.getToken(),AppApplication.getSessName() + "=" + AppApplication.getSessId(),serverEntry.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                DBManager.getInstance().onContentChanged();
                return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
            }

            return succeeded();
        }
    }



