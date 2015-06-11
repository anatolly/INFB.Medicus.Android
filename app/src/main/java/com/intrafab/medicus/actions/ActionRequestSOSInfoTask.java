package com.intrafab.medicus.actions;

import android.os.Bundle;
import android.text.TextUtils;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.Order;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Artemiy Terekhov on 11.06.2015.
 */
public class ActionRequestSOSInfoTask extends GroundyTask {
    public static final String TAG = ActionRequestSOSInfoTask.class.getName();

    public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

    @Override
    protected TaskResult doInBackground() {

        Bundle inputBundle = getArgs();
        String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

        if (!Connectivity.isNetworkConnected() || TextUtils.isEmpty(userUid)) {
            Order activeOrder = DBManager.getInstance().readObject(getContext(), Constants.Prefs.PREF_PARAM_ORDER, Order.class);

            if (activeOrder != null) {
                return succeeded()
                        .add(Constants.Extras.PARAM_ACTIVE_ORDER, activeOrder);
            }

            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        try {
            HttpRestService service = RestApiConfig.getRestService();
            Response orders = service.getAllOrders();

            List<StateEntry> list = new ArrayList<StateEntry>();
            if (orders != null) {
                TypedInput bodyOrders = orders.getBody();

                if (bodyOrders != null) {
                    String resultOrders = new String(((TypedByteArray) bodyOrders).getBytes());
                    Logger.e(TAG, "ActionRequestSOSInfoTask bodyOrders: " + resultOrders);

                    JSONArray ordersJsonList = new JSONArray(resultOrders);
                    int count = ordersJsonList.length();

                    Order activeOrder = null;
                    for (int i = 0; i < count; ++i) {
                        JSONObject orderItem = ordersJsonList.getJSONObject(i);
                        Order order = new Order(orderItem);
                        String ownerId = order.getOwnerid();
                        if (!TextUtils.isEmpty(ownerId) && userUid.equals(ownerId)) {
                            if (activeOrder == null) {
                                activeOrder = order;
                            } else {
                                String processStatus = order.getIntegerProcessStatus();
                                if (!TextUtils.isEmpty(processStatus) && processStatus.equals("4")) {
                                    activeOrder = order;
                                }
                            }
                        }
                    }

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

            Order activeOrder = DBManager.getInstance().readObject(getContext(), Constants.Prefs.PREF_PARAM_ORDER, Order.class);

            if (activeOrder != null) {
                return succeeded()
                        .add(Constants.Extras.PARAM_ACTIVE_ORDER, activeOrder);
            }

            return failed()
                    .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return failed()
                .add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
    }
}
