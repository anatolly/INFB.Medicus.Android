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

    public final static String STORAGE_List = "storage_list";

    private List<StorageInfo> strageList;

    public List<StorageInfo> getStrageList() {
        return strageList;
    }

    public void setStrageList(List<StorageInfo> strageList) {
        this.strageList = strageList;
    }


    public RequestStorageInfo() {

    }

    public RequestStorageInfo(JSONObject jsonObject) {

        if (jsonObject == null)
            return;

        strageList = new ArrayList<StorageInfo>();

        if (jsonObject.has(STORAGE_List)) {
            try {
                JSONArray itemJson = jsonObject.getJSONArray(STORAGE_List);
                for (int i = 0; i < itemJson.length(); i++) {
                    StorageInfo info = parse(itemJson.getJSONObject(i));
                    if (info != null)
                        strageList.add(info);
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
        if (strageList == null) {
            strageList = new ArrayList<StorageInfo>();
        }

        strageList.add(info);
    }

    public void addSticker(List<StorageInfo> info) {
        if (strageList == null) {
            strageList = new ArrayList<StorageInfo>();
        }

        strageList.addAll(info);
    }
}
