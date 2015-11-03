package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intrafab.medicus.utils.Converter;

import java.io.IOException;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class DataInfo implements Parcelable {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String key;
    public Object value;

    public DataInfo() {

    }

    public DataInfo(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    protected DataInfo(Parcel in) {
        key = in.readString();
        byte[] bytes = new byte[in.readInt()];

        try {
            Class theClass = Class.forName(in.readString());

            if (bytes.length > 0) {
                in.readByteArray(bytes);
                try {
                    value = theClass.cast(Converter.convertFromBytes(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                    value = null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    value = null;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<DataInfo> CREATOR = new Creator<DataInfo>() {
        @Override
        public DataInfo createFromParcel(Parcel in) {
            return new DataInfo(in);
        }

        @Override
        public DataInfo[] newArray(int size) {
            return new DataInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        try {
            byte[] bytes = Converter.convertToBytes(value);
            if (bytes.length > 0) {
                parcel.writeInt(bytes.length);
                parcel.writeString(value.getClass().getName());
                parcel.writeByteArray(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataInfo)) return false;

        DataInfo dataInfo = (DataInfo) o;

        if (getKey() != null ? !getKey().equals(dataInfo.getKey()) : dataInfo.getKey() != null)
            return false;
        return !(getValue() != null ? !getValue().equals(dataInfo.getValue()) : dataInfo.getValue() != null);

    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
