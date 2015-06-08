package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 08.06.2015.
 */
public class WrapperLogin implements Parcelable {

    public static final Creator<WrapperLogin> CREATOR = new Creator<WrapperLogin>() {
        @Override
        public WrapperLogin createFromParcel(Parcel source) {
            return new WrapperLogin(source);
        }

        @Override
        public WrapperLogin[] newArray(int size) {
            return new WrapperLogin[size];
        }
    };

    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public WrapperLogin() {

    }

    public WrapperLogin(Parcel source) {
        username = source.readString();
        password = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
    }
}
