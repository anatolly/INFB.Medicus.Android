package com.intrafab.medicus.actions;

import android.os.Bundle;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.ActivityEntry;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.data.StateEntryType;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.squareup.okhttp.RequestBody;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.io.IOException;

import okio.Buffer;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Artemiy Terekhov on 02.05.2015.
 */
public class ActionRequestChangeStatusTask extends GroundyTask {

    public static final String TAG = ActionRequestStateEntryTask.class.getName();

    public static final String ARG_NEW_STATUS = "arg_new_status";
    public static final String ARG_NEW_COMMENT = "arg_new_comment";
    public static final String ARG_STATE_ENTRY = "arg_state_entry";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        Bundle inputBundle = getArgs();
        String newStatus = inputBundle.getString(ARG_NEW_STATUS);
        String comment = inputBundle.getString(ARG_NEW_COMMENT);
        StateEntry oldEntry = inputBundle.getParcelable(ARG_STATE_ENTRY);

        StateEntry entry = null;

        try {
            HttpRestService service = RestApiConfig.getRestService();

            ActivityEntry activityEntry = new ActivityEntry();
            activityEntry.setStateStatus(newStatus);

            Response response = null;
            try {
                response = service.changeStateActivity(oldEntry.getId(), activityEntry.toJson().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            TypedInput bodyResponse = response.getBody();

            if (bodyResponse != null) {
                String resultResponse = new String(((TypedByteArray) bodyResponse).getBytes());
                Logger.e(TAG, "ActionRequestChangeStatusTask resultResponse: " + resultResponse);
            }

            // only test
//            Thread.sleep(5000);


            if (newStatus.equals(StateEntryType.STATUSES.get(1))) { //Canceled
                entry = createEntry(
                        oldEntry.getStateStart(),
                        oldEntry.getStateEnd(),
                        oldEntry.getStateDescription(),
                        oldEntry.getStateStatus(), "Canceled");
            } else if (newStatus.equals(StateEntryType.STATUSES.get(2))) { //Changed
                entry = createEntry(
                        oldEntry.getStateStart(),
                        oldEntry.getStateEnd(),
                        oldEntry.getStateDescription(),
                        oldEntry.getStateStatus(), "ChangeRequested");
            } else if (newStatus.equals(StateEntryType.STATUSES.get(3))) { //ChangeRequested
            } else {
                entry = createEntry(
                        oldEntry.getStateStart(),
                        oldEntry.getStateEnd(),
                        oldEntry.getStateDescription(),
                        oldEntry.getStateStatus(), "Accepted");
            }
            // test ended

            if (entry == null)
                return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);

            DBManager.getInstance().insertObjectToArray(getContext(), Constants.Prefs.PREF_PARAM_STATE_ENTRIES, entry, StateEntry[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return succeeded()
                .add(ARG_STATE_ENTRY, entry);
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

    private static String bodyAsString(RequestBody body) {
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(body.contentType().charset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
