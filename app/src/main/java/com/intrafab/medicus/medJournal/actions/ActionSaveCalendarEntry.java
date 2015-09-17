package com.intrafab.medicus.medJournal.actions;

import android.os.Bundle;

import com.intrafab.medicus.AppApplication;
import com.intrafab.medicus.Constants;
import com.intrafab.medicus.actions.ActionRequestUploadFileTask;
import com.intrafab.medicus.medJournal.data.PeriodCalendarEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

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
            try {
                HttpRestService service = RestApiConfig.getMedicusRestService();
                // get data from server
                List<PeriodCalendarEntry> listServerData = service.getEntry(userUid);
                // create hash map for server data
                HashMap <String, PeriodCalendarEntry> hashMapServerData = new HashMap<>(listServerData.size());
                for (PeriodCalendarEntry entry : listServerData) {
                    hashMapServerData.put(entry.getDateString(), entry);
                }
                listServerData = null;
                // get data from database
                List<PeriodCalendarEntry> dbData = DBManager.getInstance().readArrayToList(getContext(), Constants.Prefs.PREF_PARAM_PERIOD_CAL_ENTRIES, PeriodCalendarEntry[].class);
                // think that local data is more relevant that server data
                if (dbData == null || dbData.isEmpty())
                {
                    // get data
                    // тут нужно получить все данные с сервера
                }
                else {
                    for (PeriodCalendarEntry dbEntry : dbData) {
                        // если в серверных данных есть запись за одно и то же число
                        if (hashMapServerData.containsKey(dbEntry.getDateString())) {
                            PeriodCalendarEntry serverEntry = hashMapServerData.get(dbEntry.getDateString());
                            // если записи не идентичны, то в localData нужно записывать самую свежую и копировать ее на сервер
                            if (!serverEntry.equals(dbEntry)) {
                                service.uploadPCEntry( AppApplication.getToken(),dbEntry);
                            } else {
                                // если записи идентичны,то ничего делать не нужно
                            }
                        } else {
                            // если в серверных данных нет записи за это число
                            //service.refreshPCEntry(dbEntry);
                            service.uploadPCEntry( AppApplication.getToken(),dbEntry);
                        }
                    }
                    boolean isNeedToDelete = false;
                    int lastIndexInDB = dbData.size() - 1;
                    for (PeriodCalendarEntry serverEntry : hashMapServerData.values()) {
                        for (int i=0; i< dbData.size(); i++) {
                            if (serverEntry.getDateString().equals(dbData.get(i).getDateString()))
                                break;
                            if (i == lastIndexInDB)
                                isNeedToDelete = true;
                        }
                        if(isNeedToDelete){
                            //service.deletePCEntry(serverEntry.getId());
                        }
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



