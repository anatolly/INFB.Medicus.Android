package com.intrafab.medicus.actions;

import android.os.Bundle;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.Order;
import com.intrafab.medicus.data.WrapperCallback;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import org.json.JSONObject;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Artemiy Terekhov on 16.06.2015.
 */
public class ActionRequestSOSCallbackTask extends GroundyTask {
    public static final String TAG = ActionRequestLoginTask.class.getName();

    public static final String ARG_PHONE_NUMBER = "arg_phone_number";
    public static final String ARG_OWNER_ID = "arg_owner_id";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        Bundle inputBundle = getArgs();
        String phone = inputBundle.getString(ARG_PHONE_NUMBER);
        String id = inputBundle.getString(ARG_OWNER_ID);

        Logger.e(TAG, "ActionRequestSOSCallbackTask phone: " + phone);
        Logger.e(TAG, "ActionRequestSOSCallbackTask id: " + id);

        WrapperCallback newCallback = new WrapperCallback();
        newCallback.setCallbackphonenumber(phone);
        newCallback.setCallbackisrequested(true);

        try {
            HttpRestService service = RestApiConfig.getRestService();
            Response result = service.requestCallback(id, newCallback);

            if (result != null) {
                TypedInput body = result.getBody();

                if (body != null) {
                    String resultString = new String(((TypedByteArray) body).getBytes());
                    Logger.e(TAG, "ActionRequestSOSCallbackTask result: " + resultString);

                    JSONObject orderItem = new JSONObject(resultString);
                    Order activeOrder = new Order(orderItem);

                    if (activeOrder != null) {
                        Logger.e(TAG, "ActionRequestSOSInfoTask activeOrder: " + activeOrder.toJson());
                        DBManager.getInstance().insertObject(
                                getContext(), Constants.Prefs.PREF_PARAM_ORDER, activeOrder, Order.class);
                        return succeeded()
                                .add(Constants.Extras.PARAM_ACTIVE_ORDER, activeOrder);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return failed()
                .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
    }
}
