package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 28.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ExtraParametersInfo implements Parcelable {
    public static final String TAG = ExtraParametersInfo.class.getName();

    private String mKey;
    private List<DataInfo> mData;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public List<DataInfo> getData() {
        return mData;
    }

    public void setData(List<DataInfo> data) {
        this.mData = data;
    }

    public void addData(String key, Object value) {
        for (DataInfo info : mData) {
            if (info.key.equals(key)) {
                info.setValue(value);
                return;
            }
        }

        mData.add(new DataInfo(key, value));
    }

    public DataInfo getData(String key) {
        for (DataInfo info : mData) {
            if (info.key.equals(key)) {
                return info;
            }
        }

        return null;
    }

    public Object getDataValue(String key) {
        for (DataInfo info : mData) {
            if (info.key.equals(key)) {
                return info.getValue();
            }
        }

        return null;
    }

    protected ExtraParametersInfo(Parcel in) {
        mKey = in.readString();
        mData = in.createTypedArrayList(DataInfo.CREATOR);
//        if (mData == null) {
//            mData = new ArrayList<>();
//        }
//
////        in.readMap(mData, HashMap.class.getClassLoader());
////        mData = new HashMap<>(in.readInt());
////        for (int i = 0; i < mData.size(); i++) {
////            mData.put(in.readValue(Object.class.getClassLoader()), in.readValue(Object.class.getClassLoader()));
////        }
//
//
//
//        try {
//            int count = in.readInt();
//            for (int i = 0; i < count; i++) {
//                String key = in.readString();
////                Class<?> theClass = Class.forName(in.readString());
////                Class parcelableClass = theClass.asSubclass(Parcelable.class);
//                DataInfo value = in.readParcelable(DataInfo.class.getClassLoader());
//
//                mData.put(key, value);
//            }
//
//
////            for (int i = 0; i < count; i++) {
////                byte[] bytesKey = new byte[in.readInt()];
////                Class theClass = Class.forName(in.readString());
////
////                Object key = null;
////                if (bytesKey.length > 0) {
////                    in.readByteArray(bytesKey);
////                    try {
////                        key = theClass.cast(Converter.convertFromBytes(bytesKey));
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                        key = null;
////                    } catch (ClassNotFoundException e) {
////                        e.printStackTrace();
////                        key = null;
////                    }
////                }
////
////                byte[] bytesValue = new byte[in.readInt()];
////                Class theClassValue = Class.forName(in.readString());
////
////                Object value = null;
////                if (bytesValue.length > 0) {
////                    in.readByteArray(bytesValue);
////                    try {
////                        value = theClassValue.cast(Converter.convertFromBytes(bytesValue));
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                        value = null;
////                    } catch (ClassNotFoundException e) {
////                        e.printStackTrace();
////                        value = null;
////                    }
////                }
////
////                if (key != null && value != null) {
////                    mData.put(key, value);
////                }
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public ExtraParametersInfo() {
        mData = new ArrayList<>();
    }

    public static final Creator<ExtraParametersInfo> CREATOR = new Creator<ExtraParametersInfo>() {
        @Override
        public ExtraParametersInfo createFromParcel(Parcel in) {
            return new ExtraParametersInfo(in);
        }

        @Override
        public ExtraParametersInfo[] newArray(int size) {
            return new ExtraParametersInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mKey);
        parcel.writeTypedList(mData);
////        parcel.writeMap(mData);
//        parcel.writeInt(mData.size());
////        parcel.writeParcelable(mData, flags);
//
//        try {
//            for (Map.Entry<String, ? extends Parcelable> entry : mData.entrySet()) {
//                parcel.writeString(entry.getKey());
////                parcel.writeString(entry.getValue().getClass().getName());
//                parcel.writeParcelable(entry.getValue(), flags);
//
////                Object key = entry.getKey();
////                byte[] bytesKey = Converter.convertToBytes(key);
////                if (bytesKey.length > 0) {
////                    parcel.writeInt(bytesKey.length);
////                    parcel.writeString(key.getClass().getName());
////                    parcel.writeByteArray(bytesKey);
////                }
////
////                Object value = entry.getValue();
////                byte[] bytesValue = Converter.convertToBytes(value);
////                if (bytesValue.length > 0) {
////                    parcel.writeInt(bytesValue.length);
////                    parcel.writeString(value.getClass().getName());
////                    parcel.writeByteArray(bytesValue);
////                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
