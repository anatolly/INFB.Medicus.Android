package com.intrafab.medicus;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.intrafab.medicus.actions.ActionSaveMeTask;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.utils.Logger;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class AppApplication extends MultiDexApplication {

    private Account userAccount;
    private static String token;

    public Account getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
        //this.userAccount.setUid("25"); //33
    }

    public void setUserAccount(Context context, CallbacksManager callbacksManager, Account userAccount) {
        this.userAccount = userAccount;
        //this.userAccount.setUid("25");

        if (userAccount != null) {
            Groundy.create(ActionSaveMeTask.class)
                    .callback(context)
                    .callbackManager(callbacksManager)
                    .arg(ActionSaveMeTask.USER_DATA, userAccount)
                    .queueUsing(context);
        }
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        AppApplication.token = token;
    }

    @Override
    public void onCreate() {
        initCalligraphy();

        Logger.setApplicationTag("Medicus");
        Logger.setRelease(Constants.RELEASE_MODE);
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
        token = prefs.getString("isLogin", "");
        return !TextUtils.isEmpty(token);
    }

    public static void setLogin(Context context, String token) {
        setToken(token);
        SharedPreferences prefs = context.getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("isLogin", token);
        edit.commit();
    }
}
