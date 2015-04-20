package com.intrafab.medicus;

import android.content.Context;
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
}
