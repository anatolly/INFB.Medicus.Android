package com.intrafab.medicus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.intrafab.medicus.utils.Connectivity;
import com.intrafab.medicus.utils.Logger;

import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public abstract class BaseActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected Toolbar toolbar;
    protected android.support.v7.app.ActionBar bar;

    public static final String PREFS_CURRENT_LANGUAGE = "prefs_current_language";

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || context == null)
                return;

            Connectivity.updateNetworkState(context);
        }

    };

    public static class StateData {
        public String screenTag = null;
        public Intent params = null;
    }

    public Stack<StateData> mBackStack;

    protected static int mCurrentLanguageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setLanguage(this, getCurrentLanguage(this));
        setContentView(getLayoutResource());

        mBackStack = new Stack<StateData>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            bar = getSupportActionBar();
        }

        SharedPreferences prefs = getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!TextUtils.isEmpty(key) && key.equals(PREFS_CURRENT_LANGUAGE)) {
            restartActivity(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    protected abstract int getLayoutResource();

    protected void addToBackStack(String tagName, Intent params) {
        StateData data = new StateData();
        data.screenTag = tagName;
        data.params = params;

        mBackStack.push(data);
    }

    protected boolean containsBackStack(String tagName) {
        Iterator<StateData> it = mBackStack.iterator();
        while (it.hasNext()) {
            StateData data = it.next();
            if (data.screenTag.equals(tagName))
                return true;
        }

        return false;
    }

    protected StateData getFromBackStack(String tagName) {
        Iterator<StateData> it = mBackStack.iterator();
        while (it.hasNext()) {
            StateData data = it.next();
            if (data.screenTag.equals(tagName)) {
                return data;
            }
        }

        return null;
    }

    protected StateData getBackStack() {
        try {
            if (mBackStack.size() > 0)
                return mBackStack.peek();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void clearBackStack() {
        try {
            if (mBackStack.size() > 0)
                mBackStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    protected StateData popBackStack() {
        try {
            if (mBackStack.size() > 0)
                return mBackStack.pop();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void setActionBarIcon(int iconRes) {
        if (toolbar != null)
            toolbar.setNavigationIcon(iconRes);
    }

    protected void hideActionBar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected void showActionBar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);

            int primaryColor = 0;
            TypedValue a = new TypedValue();
            if (Build.VERSION.SDK_INT >= 21)
                getTheme().resolveAttribute(android.R.attr.colorPrimary, a, true);
            else
                getTheme().resolveAttribute(R.attr.primaryLight, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // primaryColor is a color
                primaryColor = a.data;
            } else {
                Logger.e("BaseActivity ", "current theme: primary color is not a color");
            }
            bar.setBackgroundDrawable(new ColorDrawable(primaryColor));
        }
    }

    protected void showTransparentActionBar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            bar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    protected static void setLanguage(Activity context, int index) {
        //Logger.e("BaseActivity", "setLanguage index: " + index);
        String[] mLangArray = context.getResources().getStringArray(R.array.pref_list_language_values);
        //Logger.e("BaseActivity", "setLanguage mLangArray[index]: " + mLangArray[index]);

        mCurrentLanguageIndex = index;

        Locale myLocale = new Locale(mLangArray[index]);
        Resources res = context.getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        Logger.e("BaseActivity", "setLanguage myLocale: " + myLocale.getLanguage());

        res.updateConfiguration(conf, dm);
        context.getBaseContext().getResources().updateConfiguration(
                context.getBaseContext().getResources().getConfiguration(),
                context.getBaseContext().getResources().getDisplayMetrics());

        Locale.setDefault(myLocale);
//        setCurrentLanguage(context, index);
        Logger.e("BaseActivity", "setLanguage myLocale: " + Locale.getDefault().getLanguage());
    }

    protected static void setCurrentLanguage(Activity context, int index) {
        SharedPreferences prefs = context.getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        SharedPreferences.Editor editLang = prefs.edit();
        editLang.putInt(PREFS_CURRENT_LANGUAGE, index);
        editLang.commit();
    }

    protected static int getCurrentLanguage(Activity context) {
        SharedPreferences prefs = context.getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        int index = prefs.getInt(PREFS_CURRENT_LANGUAGE, -1);

        if (index == -1) {
            try {
                String lang = Locale.getDefault().getISO3Language().substring(0, 2);
                if (!TextUtils.isEmpty(lang)) {
                    String[] mLangArray = context.getResources().getStringArray(R.array.pref_list_language_values);
                    int count = mLangArray.length;
                    for (int i = 0; i < count; ++i) {
                        if (mLangArray[i].equalsIgnoreCase(lang)) {

                            SharedPreferences.Editor editLang = prefs.edit();
                            editLang.putInt(PREFS_CURRENT_LANGUAGE, i);
                            editLang.commit();
                            mCurrentLanguageIndex = i;
                            return i;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                index = 0;
            }
        }

        mCurrentLanguageIndex = index;
        return index;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void restartActivity(final Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            activity.recreate();
        else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.getIntent());
                }
            });
            activity.finish();
        }
    }
}
