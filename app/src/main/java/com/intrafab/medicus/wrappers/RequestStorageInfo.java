package com.intrafab.medicus.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.intrafab.medicus.data.StorageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class RequestStorageInfo implements Parcelable {

//    public final static String STORAGE_LIST = "storage_list";

    public static final Creator<RequestStorageInfo> CREATOR = new Creator<RequestStorageInfo>() {
        @Override
        public RequestStorageInfo createFromParcel(Parcel source) {
            return new RequestStorageInfo(source);
        }

        @Override
        public RequestStorageInfo[] newArray(int size) {
            return new RequestStorageInfo[size];
        }
    };

    private List<StorageInfo> storageList;

    public List<StorageInfo> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<StorageInfo> storageList) {
        this.storageList = storageList;
    }


    public RequestStorageInfo() {

    }

    public RequestStorageInfo(Parcel source) {
        if (storageList == null)
            storageList = new ArrayList<StorageInfo>();
        source.readList(storageList, StorageInfo.class.getClassLoader());
    }

//    public RequestStorageInfo(JSONObject jsonObject) {
//
//        if (jsonObject == null)
//            return;
//
//        storageList = new ArrayList<StorageInfo>();
//
//        if (jsonObject.has(STORAGE_LIST)) {
//            try {
//                JSONArray itemJson = jsonObject.getJSONArray(STORAGE_LIST);
//                for (int i = 0; i < itemJson.length(); i++) {
//                    StorageInfo info = parse(itemJson.getJSONObject(i));
//                    if (info != null)
//                        storageList.add(info);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private StorageInfo parse(JSONObject jsonObject) {
//        if (jsonObject == null)
//            return null;
//
//        return null;//new StorageInfo(jsonObject);
//    }
//
//    public void addSticker(StorageInfo info) {
//        if (storageList == null) {
//            storageList = new ArrayList<StorageInfo>();
//        }
//
//        storageList.add(info);
//    }
//
//    public void addSticker(List<StorageInfo> info) {
//        if (storageList == null) {
//            storageList = new ArrayList<StorageInfo>();
//        }
//
//        storageList.addAll(info);
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(storageList);
    }
}
