package com.intrafab.medicus.actions;

import android.os.Bundle;
import android.text.TextUtils;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.data.ActivityEntry;
import com.intrafab.medicus.data.NodeEntry;
import com.intrafab.medicus.data.Order;
import com.intrafab.medicus.data.OrderItem;
import com.intrafab.medicus.data.ServiceSet;
import com.intrafab.medicus.data.StateEntry;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.http.HttpRestService;
import com.intrafab.medicus.http.RestApiConfig;
import com.intrafab.medicus.loaders.StateEntryListLoader;
import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;
import com.squareup.okhttp.RequestBody;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.Buffer;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class ActionRequestStateEntryTask extends GroundyTask {
    public static final String TAG = ActionRequestStateEntryTask.class.getName();

    public static final String ARG_USER_OWNER_ID = "arg_user_owner_id";

    @Override
    protected TaskResult doInBackground() {

        if (!Connectivity.isNetworkConnected()) {
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, false);
        }

        Bundle inputBundle = getArgs();
        String userUid = inputBundle.getString(ARG_USER_OWNER_ID);

        try {
            HttpRestService service = RestApiConfig.getRestService();
            Response orders = service.getAllOrders();

            List<StateEntry> list = new ArrayList<StateEntry>();
            if (orders != null) {
                TypedInput bodyOrders = orders.getBody();

                if (bodyOrders != null) {
                    String resultOrders = new String(((TypedByteArray) bodyOrders).getBytes());
                    Logger.e(TAG, "ActionRequestStateEntryTask bodyOrders: " + resultOrders);

                    JSONArray ordersJsonList = new JSONArray(resultOrders);
                    int count = ordersJsonList.length();

                    Order activeOrder = null;
//                    ArrayList<Order> userOrdersList = new ArrayList<Order>();
                    for (int i = 0; i < count; ++i) {
                        JSONObject orderItem = ordersJsonList.getJSONObject(i);
                        Order order = new Order(orderItem);
                        String ownerId = order.getOwnerid();
                        if (!TextUtils.isEmpty(ownerId) && userUid.equals(ownerId)) {
//                            userOrdersList.add(order);
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
                        ArrayList<ServiceSet> isuList = activeOrder.getServiceSets();
                        if (isuList != null) {
                            int isuCount = isuList.size();
                            for (int i = 0; i < isuCount; ++i) {
                                ServiceSet isuItem = isuList.get(i);

                                String isuId = isuItem.getId();
                                if (!TextUtils.isEmpty(isuId)) {
                                    ArrayList<StateEntry> isuListEntry = parseActivitiesEntry(service, isuId);
                                    if (isuListEntry != null && isuListEntry.size() > 0) {
                                        list.addAll(isuListEntry);
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            Thread.sleep(5000);
//
////            DBManager.getInstance().onContentChanged();
////            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
//
//            // only test
//            List<StateEntry> list = new ArrayList<StateEntry>();
//            list.add(createEntry(1430132400000L, 0L, "Airplane", "flight", "Changed"));
//            list.add(createEntry(1430134200000L, 1430137800000L, "Taxi", "taxi", "Accepted"));
//            list.add(createEntry(1430197200000L, 1430222400000L, "Checkup start", "checkup", "ChangeRequested"));
//            list.add(createEntry(1430224200000L, 1430227800000L, "Taxi", "taxi", "Changed"));
//            list.add(createEntry(1430233200000L, 1430240400000L, "Opera", "entertainment", "Accepted"));
//            list.add(createEntry(1430283600000L, 0L, "Airplane", "flight", "ChangeRequested"));
//            list.add(createEntry(1430294400000L, 1430298000000L, "Business", "default", "Accepted"));
//            list.add(createEntry(1430377200000L, 0L, "Hotel", "hotel", "Accepted"));
//            list.add(createEntry(1430388000000L, 1430398800000L, "Food", "food", "Canceled"));
//            list.add(createEntry(1430485200000L, 0L, "Airplane", "flight", "Canceled"));
//
//            RequestStateEntries entriesInfo = new RequestStateEntries();
//            entriesInfo.addEntry(list);
//            // test ended
//
//            if (entriesInfo == null) {
//                DBManager.getInstance().onContentChanged();
//                return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
//            }

//            List<StateEntry> entriesList = entriesInfo.getEntriesList();
            if (list.size() > 0) {
                DBManager.getInstance().insertArrayObject(getContext(), StateEntryListLoader.class, Constants.Prefs.PREF_PARAM_STATE_ENTRIES, list, StateEntry.class);
            } else {
                DBManager.getInstance().deleteObject(Constants.Prefs.PREF_PARAM_STATE_ENTRIES, StateEntryListLoader.class);
//                DBManager.getInstance().onContentChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DBManager.getInstance().onContentChanged();
            return failed().add(Constants.Extras.PARAM_INTERNET_AVAILABLE, true);
        }

        return succeeded();
    }

    private ArrayList<StateEntry> parseActivitiesEntry(HttpRestService service, String isuId) {
        ArrayList<ActivityEntry> activityEntryList = new ArrayList<ActivityEntry>();
        NodeEntry nodeItem = null;
        try {
            Response orderItemResponse = service.getOrderItem(isuId);

            if (orderItemResponse != null) {
                TypedInput bodyOrderItem = orderItemResponse.getBody();

                if (bodyOrderItem != null) {
                    String resultOrderItem = new String(((TypedByteArray) bodyOrderItem).getBytes());
                    Logger.e(TAG, "ActionRequestStateEntryTask resultOrderItem: " + resultOrderItem);

                    JSONObject orderItemObject = new JSONObject(resultOrderItem);
                    OrderItem orderItem = new OrderItem(orderItemObject);

                    ServiceSet serviceNode = orderItem.getService();
                    if (serviceNode != null) {
                        String nodeId = serviceNode.getId();
                        if (!TextUtils.isEmpty(nodeId)) {
                            nodeItem = parseNodeEntry(service, nodeId);
                        }
                    }

                    ArrayList<ServiceSet> activitiesList = orderItem.getActivitiesSet();
                    if (activitiesList != null) {
                        int count = activitiesList.size();
                        for (int i = 0; i < count; ++i) {
                            ServiceSet activitySet = activitiesList.get(i);
                            if (activitySet != null) {
                                String activityId = activitySet.getId();
                                if (!TextUtils.isEmpty(activityId)) {
                                    ActivityEntry activityItem = parseActivityEntry(service, activityId);
                                    if (activityItem != null) {
                                        activityEntryList.add(activityItem);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<StateEntry> resultActivityList = new ArrayList<StateEntry>();

        if (nodeItem != null) {
            Logger.e(TAG, "ActionRequestStateEntryTask activityEntryList count: " + activityEntryList.size());
            for (ActivityEntry entry : activityEntryList) {
                long stateStart = entry.getStateStart() != null ? entry.getStateStart().getTime() : 0L;
                long stateEnd = entry.getStateEnd() != null ? entry.getStateEnd().getTime() : 0L;
                String stateTitle = entry.getStateTitle();
                String stateType = nodeItem.getType();
                String stateStatus = entry.getStateStatus();
                resultActivityList.add(createEntry(stateStart, stateEnd, stateTitle, stateType, stateStatus));
            }
        }

        return resultActivityList;
    }

    private NodeEntry parseNodeEntry(HttpRestService service, String nodeId) {
        try {
            Response nodeItemResponse = service.getNodeItem(nodeId);

            if (nodeItemResponse != null) {
                TypedInput bodyNodeItem = nodeItemResponse.getBody();

                if (bodyNodeItem != null) {
                    String resultNodeItem = new String(((TypedByteArray) bodyNodeItem).getBytes());
                    Logger.e(TAG, "ActionRequestStateEntryTask resultNodeItem: " + resultNodeItem);

                    JSONObject nodeItemObject = new JSONObject(resultNodeItem);
                    return new NodeEntry(nodeItemObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ActivityEntry parseActivityEntry(HttpRestService service, String activityId) {
        try {
            Response nodeItemResponse = service.getActivityItem(activityId);

            if (nodeItemResponse != null) {
                TypedInput bodyNodeItem = nodeItemResponse.getBody();

                if (bodyNodeItem != null) {
                    String resultNodeItem = new String(((TypedByteArray) bodyNodeItem).getBytes());
                    Logger.e(TAG, "ActionRequestStateEntryTask resultActivityItem: " + resultNodeItem);

                    JSONObject nodeItemObject = new JSONObject(resultNodeItem);
                    return new ActivityEntry(nodeItemObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private StateEntry createEntry(long stateStart, long stateEnd, String stateDescription, String stateType, String stateStatus) {
        Logger.e(TAG, "ActionRequestStateEntryTask createEntry stateDescription: " + stateDescription);
        StateEntry item = new StateEntry();

        item.setStateStart(stateStart);
        item.setStateEnd(stateEnd);
        item.setStateDescription(stateDescription);
        item.setStateType(stateType);
        Logger.e(TAG, "ActionRequestStateEntryTask createEntry stateStatus: " + stateStatus);
        if (TextUtils.isEmpty(stateStatus))
            stateStatus = "Changed";
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
