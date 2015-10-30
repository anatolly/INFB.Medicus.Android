package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.intrafab.medicus.utils.Converter;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 28.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ExtraParametersInfo extends SparseArray<Object> implements Parcelable {

    protected ExtraParametersInfo(Parcel in) {
        try {
            int size = in.readInt();

            int[] keys = new int[size];
            in.readIntArray(keys);

            ArrayList<String> types = in.createStringArrayList();

            for (int i = 0; i < size; i++) {
                byte[] byteValue = new byte[in.readInt()];
                in.readByteArray(byteValue);
                Class theClass = Class.forName(types.get(i));
                put(keys[i], theClass.cast(Converter.convertFromBytes(byteValue)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExtraParametersInfo() {

    }

    public static final Creator<ExtraParametersInfo> CREATOR = new Creator<ExtraParametersInfo>() {
        @Override
        public ExtraParametersInfo createFromParcel(Parcel in) {
            return new ExtraParametersInfo(in);
//            ExtraParametersInfo read = new ExtraParametersInfo();
//            try {
//                int size = in.readInt();
//
//                int[] keys = new int[size];
//                in.readIntArray(keys);
//
//                for (int i = 0; i < size; i++) {
//                    byte[] byteValue = new byte[in.readInt()];
//                    in.readByteArray(byteValue);
//                    read.put(keys[i], Converter.convertFromBytes(byteValue));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return read;
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
        try {
            int[] keys = new int[size()];
            ArrayList<byte[]> objects = new ArrayList<>();
            ArrayList<String> types = new ArrayList<>();

            for (int i = 0; i < size(); i++) {
                keys[i] = keyAt(i);
                byte[] bytes = Converter.convertToBytes(valueAt(i));
                objects.add(bytes);
                types.add(valueAt(i).getClass().getName());
            }

            parcel.writeInt(size());
            parcel.writeIntArray(keys);
            parcel.writeStringList(types);
            for (byte[] bytes : objects) {
                parcel.writeInt(bytes.length);
                parcel.writeByteArray(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
