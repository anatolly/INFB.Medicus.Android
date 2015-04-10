package com.intrafab.medicus.utils;

import android.os.Build;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class SupportVersion {
    /**
     * @return true when the caller API version is at least Cupcake 3
     */
    public static boolean Cupcake() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE;
    }

    /**
     * @return true when the caller API version is at least Donut 4
     */
    public static boolean Donut() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT;
    }

    /**
     * @return true when the caller API version is at least Eclair 5
     */
    public static boolean Eclair() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    }

    /**
     * @return true when the caller API version is at least Froyo 8
     */
    public static boolean Froyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * @return true when the caller API version is at least GingerBread 9
     */
    public static boolean Gingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * @return true when the caller API version is at least Honeycomb 11
     */
    public static boolean Honeycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * @return true when the caller API version is at least Honeycomb 3.2, 13
     */
    public static boolean HoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * @return true when the caller API version is at least ICS 14
     */
    public static boolean IceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * @return true when the caller API version is at least JellyBean 16
     */
    public static boolean JellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * @return true when the caller API version is at least JellyBean MR1 17
     */
    public static boolean JellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * @return true when the caller API version is at least JellyBean MR2 18
     */
    public static boolean JellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * @return true when the caller API version is at least Kitkat 19
     */
    public static boolean Kitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }


    /**
     * @return true when the caller API version is at least L 20
     */
    public static boolean L() {
        return Build.VERSION.SDK_INT >= 20;
    }
}
