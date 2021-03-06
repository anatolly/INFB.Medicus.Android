package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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
    private String ownerid;
    private ArrayList<ServiceSet> serviceSets;
    private String hotLinePhone;
    private String callbackPhoneNumber;
    private boolean callbackIsRequested;
    private String medicusProcessStatus;
    private String integerProcessStatus;
    private Date created;
    private Date changed;
    private String title;

    public boolean isCallbackIsRequested() {
        return callbackIsRequested;
    }

    public void setCallbackIsRequested(boolean callbackIsRequested) {
        this.callbackIsRequested = callbackIsRequested;
    }

    public String getCallbackPhoneNumber() {
        return callbackPhoneNumber;
    }

    public void setCallbackPhoneNumber(String callbackPhoneNumber) {
        this.callbackPhoneNumber = callbackPhoneNumber;
    }

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getHotLinePhone() {
        return hotLinePhone;
    }

    public void setHotLinePhone(String hotLinePhone) {
        this.hotLinePhone = hotLinePhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getIntegerProcessStatus() {
        return integerProcessStatus;
    }

    public void setIntegerProcessStatus(String integerProcessStatus) {
        this.integerProcessStatus = integerProcessStatus;
    }

    public String getMedicusProcessStatus() {
        return medicusProcessStatus;
    }

    public void setMedicusProcessStatus(String medicusProcessStatus) {
        this.medicusProcessStatus = medicusProcessStatus;
    }

    public ArrayList<ServiceSet> getServiceSets() {
        return serviceSets;
    }

    public void setServiceSets(ArrayList<ServiceSet> serviceSets) {
        this.serviceSets = serviceSets;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Order() {
    }

    public Order(Parcel source) {
        id = source.readString();
        ownerid = source.readString();
        if (serviceSets == null)
            serviceSets = new ArrayList<ServiceSet>();
        source.readList(serviceSets, ServiceSet.class.getClassLoader());
        hotLinePhone = source.readString();
        callbackPhoneNumber = source.readString();
        callbackIsRequested = source.readInt() == 1 ? true : false;
        medicusProcessStatus = source.readString();
        integerProcessStatus = source.readString();
        long _created = source.readLong();
        created = _created == -1 ? null : new Date(_created);
        long _changed = source.readLong();
        changed = _changed == -1 ? null : new Date(_changed);
        title = source.readString();
    }

    public Order(JSONObject object) {
        if (object == null)
            return;

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        if (object.has("ownerid")) {
            this.ownerid = object.optString("ownerid");
        }

        if (object.has("services_set")) {
            JSONArray serviceSet = object.optJSONArray("services_set");
            if (serviceSet != null) {
                int count = serviceSet.length();
                for (int i = 0; i < count; ++i) {
                    JSONObject item = serviceSet.optJSONObject(i);
                    if (item != null) {
                        ServiceSet set = new ServiceSet(item);
                        if (serviceSets == null)
                            serviceSets = new ArrayList<ServiceSet>();
                        serviceSets.add(set);
                    }
                }
            }
        }

        if (object.has("communicationhotlinephone")) {
            this.hotLinePhone = object.optString("communicationhotlinephone");
        }

        if (object.has("callbackphonenumber")) {
            this.callbackPhoneNumber = object.optString("callbackphonenumber");
            if (!TextUtils.isEmpty(this.callbackPhoneNumber) && this.callbackPhoneNumber.equals("null"))
                this.callbackPhoneNumber = "";
        }

        if (object.has("callbackisrequested")) {
            this.callbackIsRequested = object.optBoolean("callbackisrequested");
        }

        if (object.has("medicusprocessstatus")) {
            this.medicusProcessStatus = object.optString("medicusprocessstatus");
        }

        if (object.has("integer_process_status")) {
            this.integerProcessStatus = object.optString("integer_process_status");
        }

        if (object.has("created")) {
            String _createdDate = object.optString("created");
            try {
                long _created = Long.parseLong(_createdDate) * 1000L;
                this.created = new Date(_created);
            } catch (Exception e) {
                e.printStackTrace();
                this.created = null;
            }
        }

        if (object.has("changed")) {
            String _changedDate = object.optString("changed");
            try {
                long _changed = Long.parseLong(_changedDate) * 1000L;
                this.changed = new Date(_changed);
            } catch (Exception e) {
                e.printStackTrace();
                this.changed = null;
            }
        }

        if (object.has("title")) {
            this.title = object.optString("title");
        }
    }

    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("id", id);
        root.put("ownerid", ownerid);

        JSONArray serviceSet = new JSONArray();
        for (ServiceSet item : serviceSets) {
            serviceSet.put(item.toJson());
        }
        root.put("services_set", serviceSet);

        root.put("communicationhotlinephone", hotLinePhone);
        root.put("callbackphonenumber", callbackPhoneNumber);
        root.put("callbackisrequested", callbackIsRequested ? "1" : "0");

        root.put("medicusprocessstatus", medicusProcessStatus);
        root.put("integer_process_status", integerProcessStatus);

        root.put("created", created == null ? "0" : String.valueOf(created.getTime()));
        root.put("changed", changed == null ? "0" : String.valueOf(changed.getTime()));

        root.put("title", title);

        return root.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerid);
        dest.writeList(serviceSets);
        dest.writeString(hotLinePhone);
        dest.writeString(callbackPhoneNumber);
        dest.writeInt(callbackIsRequested ? 1 : 0);
        dest.writeString(medicusProcessStatus);
        dest.writeString(integerProcessStatus);
        dest.writeLong(created != null ? created.getTime() : -1);
        dest.writeLong(changed != null ? changed.getTime() : -1);
        dest.writeString(title);
    }
}
