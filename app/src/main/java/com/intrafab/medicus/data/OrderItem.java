package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Artemiy Terekhov on 28.05.2015.
 */
public class OrderItem implements Parcelable {

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel source) {
            return new OrderItem(source);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    private String id;
    private ArrayList<String> contractItem;
    private ArrayList<String> service;
    private ArrayList<String> activitiesSet;
    private ArrayList<String> priceContractItem;


    public OrderItem() {
    }

    public OrderItem(Parcel source) {
        id = source.readString();
        if (contractItem == null)
            contractItem = new ArrayList<String>();
        source.readList(contractItem, String.class.getClassLoader());
        if (service == null)
            service = new ArrayList<String>();
        source.readList(service, String.class.getClassLoader());
        if (activitiesSet == null)
            activitiesSet = new ArrayList<String>();
        source.readList(activitiesSet, String.class.getClassLoader());
        if (priceContractItem == null)
            priceContractItem = new ArrayList<String>();
        source.readList(priceContractItem, String.class.getClassLoader());
    }

    public OrderItem(JSONObject object) {
        if (object == null)
            return;

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        String code = Locale.getDefault().getISO3Language();
        if (TextUtils.isEmpty(code)) {
            code = "und";
        }

        contractItem = new ArrayList<String>();
        if (object.has("field_contractitem")) {
            JSONObject serviceSet = object.optJSONObject("field_contractitem");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("target_id")) {
                                    String target = item.optString("target_id");
                                    if (!TextUtils.isEmpty(target))
                                        contractItem.add(target);
                                }
                            }
                        }
                    }
                }
            }
        }

        service = new ArrayList<String>();
        if (object.has("field_service")) {
            JSONObject serviceSet = object.optJSONObject("field_service");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("target_id")) {
                                    String target = item.optString("target_id");
                                    if (!TextUtils.isEmpty(target))
                                        service.add(target);
                                }
                            }
                        }
                    }
                }
            }
        }

        activitiesSet = new ArrayList<String>();
        if (object.has("field_activities_set")) {
            JSONObject serviceSet = object.optJSONObject("field_activities_set");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("target_id")) {
                                    String target = item.optString("target_id");
                                    if (!TextUtils.isEmpty(target))
                                        activitiesSet.add(target);
                                }
                            }
                        }
                    }
                }
            }
        }

        priceContractItem = new ArrayList<String>();
        if (object.has("field_pricecontractitem")) {
            JSONObject serviceSet = object.optJSONObject("field_pricecontractitem");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("target_id")) {
                                    String target = item.optString("target_id");
                                    if (!TextUtils.isEmpty(target))
                                        priceContractItem.add(target);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("id", id);

        JSONObject contractitem = new JSONObject();
        JSONArray lang = new JSONArray();
        for (String item : contractItem) {
            JSONObject target = new JSONObject();
            target.put("target_id", item);
            lang.put(target);
        }
        contractitem.put("und", lang);

        root.put("field_contractitem", contractitem);

        JSONObject serviceField = new JSONObject();
        JSONArray lang2 = new JSONArray();
        for (String item : service) {
            JSONObject target = new JSONObject();
            target.put("target_id", item);
            lang2.put(target);
        }
        serviceField.put("und", lang2);

        root.put("field_service", serviceField);

        JSONObject activities_set = new JSONObject();
        JSONArray lang3 = new JSONArray();
        for (String item : activitiesSet) {
            JSONObject target = new JSONObject();
            target.put("target_id", item);
            lang3.put(target);
        }
        activities_set.put("und", lang3);

        root.put("field_activities_set", activities_set);

        JSONObject pricecontractitem = new JSONObject();
        JSONArray lang4 = new JSONArray();
        for (String item : priceContractItem) {
            JSONObject target = new JSONObject();
            target.put("target_id", item);
            lang4.put(target);
        }
        pricecontractitem.put("und", lang4);

        root.put("field_pricecontractitem", pricecontractitem);

        return root.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeList(contractItem);
        dest.writeList(service);
        dest.writeList(activitiesSet);
        dest.writeList(priceContractItem);
    }
}
