package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Artemiy Terekhov on 01.05.2015.
 */
public class ContactDetails implements Parcelable {

    public static final Creator<ContactDetails> CREATOR = new Creator<ContactDetails>() {
        @Override
        public ContactDetails createFromParcel(Parcel source) {
            return new ContactDetails(source);
        }

        @Override
        public ContactDetails[] newArray(int size) {
            return new ContactDetails[size];
        }
    };

    public ContactDetails() {
    }

    public ContactDetails(Parcel source) {
    }

    public ContactDetails(JSONObject object) {
        if (object == null)
            return;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
