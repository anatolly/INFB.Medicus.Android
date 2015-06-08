package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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
    private ArrayList<ServiceSet> contractItem;
    private ArrayList<ServiceSet> activitiesSet;
    private ArrayList<ServiceSet> priceContractItem;
    private String activitiesType;
    private String remoteDoc;
    private String medicusProcessStatus;
    private String parentCompositeServiceUseId;
    private String integerProcessStatus;
    private Date created;
    private Date changed;
    private ServiceSet service;

    public ServiceSet getService() {
        return service;
    }

    public void setService(ServiceSet service) {
        this.service = service;
    }

    public ArrayList<ServiceSet> getActivitiesSet() {
        return activitiesSet;
    }

    public void setActivitiesSet(ArrayList<ServiceSet> activitiesSet) {
        this.activitiesSet = activitiesSet;
    }

    public String getActivitiesType() {
        return activitiesType;
    }

    public void setActivitiesType(String activitiesType) {
        this.activitiesType = activitiesType;
    }

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public ArrayList<ServiceSet> getContractItem() {
        return contractItem;
    }

    public void setContractItem(ArrayList<ServiceSet> contractItem) {
        this.contractItem = contractItem;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getParentCompositeServiceUseId() {
        return parentCompositeServiceUseId;
    }

    public void setParentCompositeServiceUseId(String parentCompositeServiceUseId) {
        this.parentCompositeServiceUseId = parentCompositeServiceUseId;
    }

    public ArrayList<ServiceSet> getPriceContractItem() {
        return priceContractItem;
    }

    public void setPriceContractItem(ArrayList<ServiceSet> priceContractItem) {
        this.priceContractItem = priceContractItem;
    }

    public String getRemoteDoc() {
        return remoteDoc;
    }

    public void setRemoteDoc(String remoteDoc) {
        this.remoteDoc = remoteDoc;
    }

    public OrderItem() {
    }

    public OrderItem(Parcel source) {
        id = source.readString();
        if (contractItem == null)
            contractItem = new ArrayList<ServiceSet>();
        source.readList(contractItem, ServiceSet.class.getClassLoader());
        if (activitiesSet == null)
            activitiesSet = new ArrayList<ServiceSet>();
        source.readList(activitiesSet, ServiceSet.class.getClassLoader());
        if (priceContractItem == null)
            priceContractItem = new ArrayList<ServiceSet>();
        source.readList(priceContractItem, ServiceSet.class.getClassLoader());
        activitiesType = source.readString();
        remoteDoc = source.readString();
        medicusProcessStatus = source.readString();
        parentCompositeServiceUseId = source.readString();
        integerProcessStatus = source.readString();
        long _created = source.readLong();
        created = _created == -1 ? null : new Date(_created);
        long _changed = source.readLong();
        changed = _changed == -1 ? null : new Date(_changed);
        service = source.readParcelable(ServiceSet.class.getClassLoader());
    }

    public OrderItem(JSONObject object) {
        if (object == null)
            return;

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        if (object.has("contractitem")) {
            JSONArray serviceSet = object.optJSONArray("contractitem");
            if (serviceSet != null) {
                int count = serviceSet.length();
                for (int i = 0; i < count; ++i) {
                    JSONObject item = serviceSet.optJSONObject(i);
                    if (item != null) {
                        ServiceSet set = new ServiceSet(item);
                        if (contractItem == null)
                            contractItem = new ArrayList<ServiceSet>();
                        contractItem.add(set);
                    }
                }
            }
        }

        if (object.has("medicus_activities_set")) {
            JSONArray serviceSet = object.optJSONArray("medicus_activities_set");
            if (serviceSet != null) {
                int count = serviceSet.length();
                for (int i = 0; i < count; ++i) {
                    JSONObject item = serviceSet.optJSONObject(i);
                    if (item != null) {
                        ServiceSet set = new ServiceSet(item);
                        if (activitiesSet == null)
                            activitiesSet = new ArrayList<ServiceSet>();
                        activitiesSet.add(set);
                    }
                }
            }
        }

        if (object.has("pricecontractitem")) {
            JSONArray serviceSet = object.optJSONArray("pricecontractitem");
            if (serviceSet != null) {
                int count = serviceSet.length();
                for (int i = 0; i < count; ++i) {
                    JSONObject item = serviceSet.optJSONObject(i);
                    if (item != null) {
                        ServiceSet set = new ServiceSet(item);
                        if (priceContractItem == null)
                            priceContractItem = new ArrayList<ServiceSet>();
                        priceContractItem.add(set);
                    }
                }
            }
        }

        if (object.has("activities_type")) {
            this.activitiesType = object.optString("activities_type");
        }

        if (object.has("remote_doc")) {
            this.remoteDoc = object.optString("remote_doc");
        }

        if (object.has("medicusprocessstatus")) {
            this.medicusProcessStatus = object.optString("medicusprocessstatus");
        }

        if (object.has("parent_composite_serviceuse_id")) {
            this.parentCompositeServiceUseId = object.optString("parent_composite_serviceuse_id");
        }

        if (object.has("integer_process_status")) {
            this.integerProcessStatus = object.optString("integer_process_status");
        }

        if (object.has("created")) {
            try {
                String _createdDate = object.optString("created");
                long _value = Long.parseLong(_createdDate) * 1000L;
                created = _value == -1 ? null : new Date(_value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (object.has("changed")) {
            try {
                String _changedDate = object.optString("changed");
                long _value = Long.parseLong(_changedDate) * 1000L;
                changed = _value == -1 ? null : new Date(_value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (object.has("service")) {
            JSONObject item = object.optJSONObject("service");
            if (item != null) {
                this.service = new ServiceSet(item);
            }
        }
    }

    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("id", id);

        JSONArray contractItemJson = new JSONArray();
        for (ServiceSet item : contractItem) {
            contractItemJson.put(item.toJson());
        }
        root.put("contractitem", contractItemJson);

        JSONArray activitiesSetJson = new JSONArray();
        for (ServiceSet item : activitiesSet) {
            activitiesSetJson.put(item.toJson());
        }
        root.put("activities_set", activitiesSetJson);

        JSONArray priceContractItemJson = new JSONArray();
        for (ServiceSet item : priceContractItem) {
            priceContractItemJson.put(item.toJson());
        }
        root.put("pricecontractitem", priceContractItemJson);

        root.put("activities_type", activitiesType);
        root.put("remote_doc", remoteDoc);
        root.put("medicusprocessstatus", medicusProcessStatus);
        root.put("parent_composite_serviceuse_id", parentCompositeServiceUseId);
        root.put("integer_process_status", integerProcessStatus);

        root.put("created", created == null ? "0" : String.valueOf(created.getTime()));
        root.put("changed", changed == null ? "0" : String.valueOf(changed.getTime()));

        if (service != null) {
            root.put("service", service.toJson());
        }

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
        dest.writeList(activitiesSet);
        dest.writeList(priceContractItem);
        dest.writeString(activitiesType);
        dest.writeString(remoteDoc);
        dest.writeString(medicusProcessStatus);
        dest.writeString(parentCompositeServiceUseId);
        dest.writeString(integerProcessStatus);
        dest.writeLong(created != null ? created.getTime() : -1);
        dest.writeLong(changed != null ? changed.getTime() : -1);
        dest.writeParcelable(service, flags);
    }
}
