package com.intrafab.medicus.actions;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.loaders.StateEntryListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.wrappers.RequestStateEntries;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class ActionRequestStateEntryTask extends GroundyTask {

    public static final String INTERNET_AVAILABLE = "internet_available";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(INTERNET_AVAILABLE, false);
        }

        try {
            HttpRestService service = RestApiConfig.getRestService();
            //RequestStateEntries entriesInfo = service.loadStateEntries();

            Thread.sleep(5000);

            // only test
            List<StateEntry> list = new ArrayList<StateEntry>();
            list.add(createEntry(1430132400000L, 0L, "Airplane", "flight", "Changed"));
            list.add(createEntry(1430134200000L, 1430137800000L, "Taxi", "taxi", "Accepted"));
            list.add(createEntry(1430197200000L, 1430222400000L, "Checkup start", "checkup", "ChangeRequested"));
            list.add(createEntry(1430224200000L, 1430227800000L, "Taxi", "taxi", "Changed"));
            list.add(createEntry(1430233200000L, 1430240400000L, "Opera", "entertainment", "Accepted"));
            list.add(createEntry(1430283600000L, 0L, "Airplane", "flight", "ChangeRequested"));
            list.add(createEntry(1430294400000L, 1430298000000L, "Business", "default", "Accepted"));
            list.add(createEntry(1430377200000L, 0L, "Hotel", "hotel", "Accepted"));
            list.add(createEntry(1430388000000L, 1430398800000L, "Food", "food", "Canceled"));
            list.add(createEntry(1430485200000L, 0L, "Airplane", "flight", "Canceled"));

            RequestStateEntries entriesInfo = new RequestStateEntries();
            entriesInfo.addEntry(list);
            // test ended

            if (entriesInfo == null)
                return failed();

            List<StateEntry> entriesList = entriesInfo.getEntriesList();
            if (entriesList.size() > 0)
                DBManager.getInstance().insertArrayObject(getContext(), StateEntryListLoader.class, Constants.Prefs.PREF_PARAM_STATE_ENTRIES, entriesList, StateEntry.class);
        } catch (Exception e) {
            e.printStackTrace();
            return failed();
        }

        return succeeded();
    }

    private StateEntry createEntry(long stateStart, long stateEnd, String stateDescription, String stateType, String stateStatus) {
        StateEntry item = new StateEntry();

        item.setStateStart(stateStart);
        item.setStateEnd(stateEnd);
        item.setStateDescription(stateDescription);
        item.setStateType(stateType);
        item.setStateStatus(stateStatus);

        return item;
    }
}
