package com.intrafab.medicus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.intrafab.medicus.utils.Connectivity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public abstract class BaseActivity extends ActionBarActivity {
    protected Toolbar toolbar;
    protected android.support.v7.app.ActionBar bar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mBackStack = new Stack<StateData>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            bar = getSupportActionBar();
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
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorLightPrimary)));
        }
    }

    protected void showTransparentActionBar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            bar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
