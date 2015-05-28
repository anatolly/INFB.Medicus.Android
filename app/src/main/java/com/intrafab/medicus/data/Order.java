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
public class Order implements Parcelable {

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    private String id;
    private String orderId;
    private String codeLang;
    private ArrayList<String> hotLinePhone;
    private ArrayList<String> callbackPhoneNumber;
    private boolean callbackIsRequested;


    public Order() {
    }

    public Order(Parcel source) {
        id = source.readString();
        orderId = source.readString();
        codeLang = source.readString();
        if (hotLinePhone == null)
            hotLinePhone = new ArrayList<String>();
        source.readList(hotLinePhone, String.class.getClassLoader());
        if (callbackPhoneNumber == null)
            callbackPhoneNumber = new ArrayList<String>();
        source.readList(callbackPhoneNumber, String.class.getClassLoader());
        callbackIsRequested = source.readInt() == 1 ? true : false;
    }

    public Order(JSONObject object) {
        if (object == null)
            return;

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        String code = Locale.getDefault().getISO3Language();
        if (TextUtils.isEmpty(code)) {
            code = "und";
        }

        if (object.has("field_services_set")) {
            JSONObject serviceSet = object.optJSONObject("field_services_set");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("target_id")) {
                                    orderId = item.optString("target_id");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        hotLinePhone = new ArrayList<String>();
        if (object.has("field_communicationhotlinephone")) {
            JSONObject serviceSet = object.optJSONObject("field_communicationhotlinephone");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("value")) {
                                    String number = item.optString("value");
                                    if (!TextUtils.isEmpty(number))
                                        hotLinePhone.add(number);
                                }
                            }
                        }
                    }
                }
            }
        }

        callbackPhoneNumber = new ArrayList<String>();
        if (object.has("field_callbackphonenumber")) {
            JSONObject serviceSet = object.optJSONObject("field_callbackphonenumber");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("value")) {
                                    String number = item.optString("value");
                                    if (!TextUtils.isEmpty(number))
                                        callbackPhoneNumber.add(number);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (object.has("field_callbackisrequested")) {
            JSONObject serviceSet = object.optJSONObject("field_callbackisrequested");
            if (serviceSet != null) {
                if (serviceSet.has("und")) {
                    JSONArray und = serviceSet.optJSONArray("und");
                    if (und != null) {
                        int count = und.length();
                        for (int i = 0; i < count; ++i) {
                            JSONObject item = und.optJSONObject(i);
                            if (item != null) {
                                if (item.has("value")) {
                                    String value = item.optString("value");
                                    if (!TextUtils.isEmpty(value)) {
                                        callbackIsRequested = value.equals("1");
                                    }
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

        JSONObject serviceSet = new JSONObject();
        JSONArray lang = new JSONArray();
        JSONObject target = new JSONObject();
        target.put("target_id", orderId);
        lang.put(target);
        serviceSet.put("und", lang);

        root.put("field_services_set", serviceSet);

        JSONObject communicationhotlinephone = new JSONObject();
        JSONArray lang2 = new JSONArray();
        for (String item : hotLinePhone) {
            JSONObject value = new JSONObject();
            value.put("value", item);
            lang2.put(value);
        }
        communicationhotlinephone.put("und", lang2);

        root.put("field_communicationhotlinephone", communicationhotlinephone);

        JSONObject callbackphonenumber = new JSONObject();
        JSONArray lang3 = new JSONArray();
        for (String item : callbackPhoneNumber) {
            JSONObject value = new JSONObject();
            value.put("value", item);
            lang3.put(value);
        }
        callbackphonenumber.put("und", lang3);

        root.put("field_callbackphonenumber", callbackphonenumber);

        JSONObject callbackisrequested = new JSONObject();
        JSONArray lang4 = new JSONArray();
        JSONObject value = new JSONObject();
        value.put("value", callbackIsRequested ? "1" : "0");
        lang4.put(value);
        serviceSet.put("und", lang4);

        root.put("field_callbackisrequested", callbackisrequested);

        return root.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(orderId);
        dest.writeString(codeLang);
        dest.writeList(hotLinePhone);
        dest.writeList(callbackPhoneNumber);
        dest.writeInt(callbackIsRequested ? 1 : 0);
    }
}
