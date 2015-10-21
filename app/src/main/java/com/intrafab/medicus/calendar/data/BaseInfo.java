package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class BaseInfo implements Parcelable {
    public static final String TAG = BaseInfo.class.getName();

    private String mId;
    private String mHeader;
    private String mSubHeader;
    private String mDescription;
    private String mIcon;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String header) {
        this.mHeader = header;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getSubHeader() {
        return mSubHeader;
    }

    public void setSubHeader(String subHeader) {
        this.mSubHeader = subHeader;
    }

    protected BaseInfo(Parcel in) {
        mId = in.readString();
        mHeader = in.readString();
        mSubHeader = in.readString();
        mDescription = in.readString();
        mIcon = in.readString();
    }

    public static final Creator<BaseInfo> CREATOR = new Creator<BaseInfo>() {
        @Override
        public BaseInfo createFromParcel(Parcel in) {
            return new BaseInfo(in);
        }

        @Override
        public BaseInfo[] newArray(int size) {
            return new BaseInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mHeader);
        parcel.writeString(mSubHeader);
        parcel.writeString(mDescription);
        parcel.writeString(mIcon);
    }
}
