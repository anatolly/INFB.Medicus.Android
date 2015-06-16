package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 16.06.2015.
 */
public class WrapperStatusList implements Parcelable {

    public static final Creator<WrapperStatusList> CREATOR = new Creator<WrapperStatusList>() {
        @Override
        public WrapperStatusList createFromParcel(Parcel source) {
            return new WrapperStatusList(source);
        }

        @Override
        public WrapperStatusList[] newArray(int size) {
            return new WrapperStatusList[size];
        }
    };

    public List<StateEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<StateEntry> entries) {
        this.entries = entries;
    }

    private List<StateEntry> entries;

    public WrapperStatusList() {

    }

    public WrapperStatusList(Parcel source) {
        if (entries == null)
            entries = new ArrayList<StateEntry>();
        source.readList(entries, StateEntry.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(entries);
    }
}
