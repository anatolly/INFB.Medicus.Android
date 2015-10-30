package com.intrafab.medicus.calendar.data;

import android.os.Parcel;

import com.intrafab.medicus.utils.Converter;

import java.io.IOException;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ParametersInfo extends BaseInfo {
    public static final String TAG = ParametersInfo.class.getName();

    private EType mType;
    private Object mValue;

    public EType getType() {
        return mType;
    }

    public void setType(EType type) {
        this.mType = type;
    }

    public Object getValue() {
        return mValue;
    }

    public void setValue(Object value) {
        this.mValue = value;
    }

    public ParametersInfo() {
        super();
    }

    protected ParametersInfo(Parcel in) {
        super(in);
        mType = in.readParcelable(EType.class.getClassLoader());

        byte[] bytes = new byte[in.readInt()];

        try {
            Class theClass = Class.forName(in.readString());

            if (bytes.length > 0) {
                in.readByteArray(bytes);
                try {
                    mValue = theClass.cast(Converter.convertFromBytes(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                    mValue = null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    mValue = null;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<ParametersInfo> CREATOR = new Creator<ParametersInfo>() {
        @Override
        public ParametersInfo createFromParcel(Parcel in) {
            return new ParametersInfo(in);
        }

        @Override
        public ParametersInfo[] newArray(int size) {
            return new ParametersInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeParcelable(mType, i);
        try {
            byte[] bytes = Converter.convertToBytes(mValue);
            if (bytes.length > 0) {
                parcel.writeInt(bytes.length);
                parcel.writeString(mValue.getClass().getName());
                parcel.writeByteArray(bytes);
            } else {
                parcel.writeInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            parcel.writeInt(0);
        }
    }
}
