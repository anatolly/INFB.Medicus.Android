package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 17.06.2015.
 */
public class WrapperCallback implements Parcelable {

    public static final Creator<WrapperCallback> CREATOR = new Creator<WrapperCallback>() {
        @Override
        public WrapperCallback createFromParcel(Parcel source) {
            return new WrapperCallback(source);
        }

        @Override
        public WrapperCallback[] newArray(int size) {
            return new WrapperCallback[size];
        }
    };

    public String getCallbackphonenumber() {
        return callbackphonenumber;
    }

    public void setCallbackphonenumber(String callbackphonenumber) {
        this.callbackphonenumber = callbackphonenumber;
    }

    public boolean getCallbackisrequested() {
        return callbackisrequested;
    }

    public void setCallbackisrequested(boolean callbackisrequested) {
        this.callbackisrequested = callbackisrequested;
    }

    private String callbackphonenumber;
    private boolean callbackisrequested;

    public WrapperCallback() {

    }

    public WrapperCallback(Parcel source) {
        callbackphonenumber = source.readString();
        callbackisrequested = source.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(callbackphonenumber);
        dest.writeInt(callbackisrequested ? 1 : 0);
    }
}
