package com.intrafab.medicus.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by Artemiy Terekhov on 20.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class Converter {

    public static byte[] convertToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutput out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (Exception ex) {
                // ignore close exception
            }

            try {
                out.close();
            } catch (Exception ex) {
                // ignore close exception
            }
        }
    }

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInput in = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bis);
            return in.readObject();
        } finally {
            try {
                bis.close();
            } catch (Exception ex) {
                // ignore close exception
            }

            try {
                in.close();
            } catch (Exception ex) {
                // ignore close exception
            }
        }
    }
}
