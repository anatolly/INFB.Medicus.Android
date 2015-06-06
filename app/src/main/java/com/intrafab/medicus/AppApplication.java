package com.intrafab.medicus;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.intrafab.medicus.utils.Logger;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class AppApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        initCalligraphy();

        Logger.setApplicationTag("Medicus");
        Logger.setRelease(Constants.RELEASE_MODE);
    }

    public static Context getContext() {
        return getContext();
    }

    public static AppApplication getApplication(Context context) {
        if (context instanceof AppApplication) {
            return (AppApplication) context;
        }
        return (AppApplication) context.getApplicationContext();
    }

    private static void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        return prefs.getBoolean("isLogin", false);
    }

    public static void setLogin(Context context, boolean isLoggedIn) {
        SharedPreferences prefs = context.getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isLogin", isLoggedIn);
        edit.commit();
    }
}
