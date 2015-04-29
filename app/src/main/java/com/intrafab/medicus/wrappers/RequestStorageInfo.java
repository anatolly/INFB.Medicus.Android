package com.intrafab.medicus.wrappers;

import com.intrafab.medicus.data.StorageInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class RequestStorageInfo {

    public final static String STORAGE_LIST = "storage_list";

    private List<StorageInfo> storageList;

    public List<StorageInfo> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<StorageInfo> storageList) {
        this.storageList = storageList;
    }


    public RequestStorageInfo() {

    }

    public RequestStorageInfo(JSONObject jsonObject) {

        if (jsonObject == null)
            return;

        storageList = new ArrayList<StorageInfo>();

        if (jsonObject.has(STORAGE_LIST)) {
            try {
                JSONArray itemJson = jsonObject.getJSONArray(STORAGE_LIST);
                for (int i = 0; i < itemJson.length(); i++) {
                    StorageInfo info = parse(itemJson.getJSONObject(i));
                    if (info != null)
                        storageList.add(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private StorageInfo parse(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return new StorageInfo(jsonObject);
    }

    public void addSticker(StorageInfo info) {
        if (storageList == null) {
            storageList = new ArrayList<StorageInfo>();
        }

        storageList.add(info);
    }

    public void addSticker(List<StorageInfo> info) {
        if (storageList == null) {
            storageList = new ArrayList<StorageInfo>();
        }

        storageList.addAll(info);
    }
}
