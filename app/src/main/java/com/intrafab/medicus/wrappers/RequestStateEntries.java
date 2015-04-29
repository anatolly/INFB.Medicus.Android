package com.intrafab.medicus.wrappers;

import com.intrafab.medicus.data.StateEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class RequestStateEntries {

    public final static String ENTRY_LIST = "entry_list";

    private List<StateEntry> entriesList;

    public List<StateEntry> getEntriesList() {
        return entriesList;
    }

    public void setEntriesList(List<StateEntry> entriesList) {
        this.entriesList = entriesList;
    }

    public RequestStateEntries() {

    }

    public RequestStateEntries(JSONObject jsonObject) {

        if (jsonObject == null)
            return;

        entriesList = new ArrayList<StateEntry>();

        if (jsonObject.has(ENTRY_LIST)) {
            try {
                JSONArray itemJson = jsonObject.getJSONArray(ENTRY_LIST);
                for (int i = 0; i < itemJson.length(); i++) {
                    StateEntry info = parse(itemJson.getJSONObject(i));
                    if (info != null)
                        entriesList.add(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private StateEntry parse(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return new StateEntry(jsonObject);
    }

    public void addEntry(StateEntry info) {
        if (entriesList == null) {
            entriesList = new ArrayList<StateEntry>();
        }

        entriesList.add(info);
    }

    public void addEntry(List<StateEntry> info) {
        if (entriesList == null) {
            entriesList = new ArrayList<StateEntry>();
        }

        entriesList.addAll(info);
    }
}