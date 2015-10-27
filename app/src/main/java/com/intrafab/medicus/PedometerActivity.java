package com.intrafab.medicus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.intrafab.medicus.fragments.PlaceholderPedometerMonthFragment;
import com.intrafab.medicus.fragments.PlaceholderPedometerTodayFragment;
import com.intrafab.medicus.fragments.PlaceholderPedometerWeekFragment;
import com.intrafab.medicus.fragments.PlaceholderPedometerYearFragment;
import com.intrafab.medicus.pedometer.OnEventUpdateCalories;
import com.intrafab.medicus.pedometer.OnEventUpdateDistance;
import com.intrafab.medicus.pedometer.OnEventUpdatePace;
import com.intrafab.medicus.pedometer.OnEventUpdateSpeed;
import com.intrafab.medicus.pedometer.OnEventUpdateStateUi;
import com.intrafab.medicus.pedometer.OnEventUpdateStep;
import com.intrafab.medicus.pedometer.ServiceEvent;
import com.intrafab.medicus.pedometer.Settings;
import com.intrafab.medicus.pedometer.StepService;
import com.intrafab.medicus.utils.EventBus;
import com.intrafab.medicus.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class PedometerActivity extends BaseActivity
        implements View.OnClickListener,
        PlaceholderPedometerTodayFragment.OnClickListener {
    private static final String TAG = PedometerActivity.class.getName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SharedPreferences mSettings;
    private Settings mPedometerSettings;

    private boolean mIsRunning = false;
    private boolean mNeedStart = false;
//    private boolean mNeedResume = false;

    //private StepService mService;
    private Messenger mServiceMessenger = null;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Logger.d(TAG, "[ACTIVITY] onServiceConnected");
            mServiceMessenger = new Messenger(service);
            mIsBound = true;

//            mService = ((StepService.StepBinder) service).getService();
//
//            mService.registerCallback(PedometerActivity.this);
//            mService.loadSettings();
//            sendEvent(new ServiceEvent(ServiceEvent.START));
            //mIsRunning = true;
            if (mNeedStart) {
                sendMessageStart();
                mNeedStart = false;
            }
//            else if (mNeedResume) {
//                sendMessageResume();
//                mNeedResume = false;
//            }
            sendMessageResume();
            //onStateChanged(null);
        }

        public void onServiceDisconnected(ComponentName className) {
            Logger.d(TAG, "[ACTIVITY] onServiceDisconnected");
            mServiceMessenger = null;
            mIsBound = false;
            //mIsRunning = false;
            onStateChanged(null);
        }
    };

    private class CommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceEvent.PACE_CHANGED:
                    onPaceChanged(msg);
                    break;

                case ServiceEvent.CALORIES_CHANGED:
                    onCaloriesChanged(msg);
                    break;

                case ServiceEvent.DISTANCE_CHANGED:
                    onDistanceChanged(msg);
                    break;

                case ServiceEvent.SPEED_CHANGED:
                    onSpeedChanged(msg);
                    break;

                case ServiceEvent.STEP_CHANGED:
                    onStepChanged(msg);
                    break;

                case ServiceEvent.START_DETECTOR:
                    onStartDetector(msg);
                    break;

                case ServiceEvent.STOP_DETECTOR:
                    onStopDetector(msg);
                    break;

//                case ServiceEvent.PAUSE:
//                    onPause(msg);
//                    break;
//
                case ServiceEvent.RESUME:
                    onResume(msg);
                    break;
            }
        }
    }

    private final Messenger mCallbackMessenger = new Messenger(new CommandHandler());

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pedometer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.header_pedometer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        bindStepService();
        //startService(new Intent(this, StepService.class));
    }

    @Override
    protected void onStop() {
        Logger.i(TAG, "[ACTIVITY] onStop");
        //stopStepService();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedometer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            PedometerSettingsActivity.launch(this);
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaceholderPedometerTodayFragment(), getString(R.string.tab_today));
        adapter.addFragment(new PlaceholderPedometerWeekFragment(), getString(R.string.tab_week));
        adapter.addFragment(new PlaceholderPedometerMonthFragment(), getString(R.string.tab_month));
        adapter.addFragment(new PlaceholderPedometerYearFragment(), getString(R.string.tab_year));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPedometerStarted() {
        Logger.d(TAG, "[ACTIVITY] onPedometerStarted");
        //startStepService();
        if (!mIsBound) {
            mNeedStart = true;
            bindStepService();
        } else {
            sendMessageStart();
        }
    }

    @Override
    public void onPedometerPaused() {
        Logger.d(TAG, "[ACTIVITY] onPedometerPaused");

        //stopStepService();
        sendMessageStop();
        //stopService(new Intent(this, StepService.class));
    }

    @Override
    public void onOpenStatisticsToday() {
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations( R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
//                        .show( m_topFragment )
//                        .commit()
    }

    @Override
    public void onStepModeActivated() {

    }

    @Override
    public void onDistanceModeActivated() {

    }

    public void onPaceChanged(Message msg) {
        if (msg == null)
            return;

        Bundle data = msg.getData();
        if (data == null)
            return;

        long value = data.getLong(ServiceEvent.PACE_VALUE, 0L);

        EventBus.getInstance().post(new OnEventUpdatePace(value));
    }

    public void onCaloriesChanged(Message msg) {
        if (msg == null)
            return;

        Bundle data = msg.getData();
        if (data == null)
            return;

        float value = data.getFloat(ServiceEvent.CALORIES_VALUE, 0f);

        EventBus.getInstance().post(new OnEventUpdateCalories(value));
    }

    public void onDistanceChanged(Message msg) {
        if (msg == null)
            return;

        Bundle data = msg.getData();
        if (data == null)
            return;

        float value = data.getFloat(ServiceEvent.DISTANCE_VALUE, 0f);

        EventBus.getInstance().post(new OnEventUpdateDistance(value));
    }

    public void onSpeedChanged(Message msg) {
        if (msg == null)
            return;

        Bundle data = msg.getData();
        if (data == null)
            return;

        float value = data.getFloat(ServiceEvent.SPEED_VALUE, 0f);

        EventBus.getInstance().post(new OnEventUpdateSpeed(value));
    }

    public void onStepChanged(Message msg) {
        if (msg == null)
            return;

        Bundle data = msg.getData();
        if (data == null)
            return;

        long value = data.getLong(ServiceEvent.STEP_VALUE, 0L);

        EventBus.getInstance().post(new OnEventUpdateStep(value));
    }

    public void onStartDetector(Message msg) {
        //sendEvent(new ServiceEvent(ServiceEvent.START));
        onStepChanged(null);
    }

    public void onStopDetector(Message msg) {
        unbindStepService();

    }

    public void onStateChanged(Message msg) {
//        if (msg == null)
//            return;

//        Bundle data = msg.getData();
//        if (data == null)
//            return;

        EventBus.getInstance().post(new OnEventUpdateStateUi(mIsRunning));
    }

//    public void onPause(Message msg) {
//        if (mIsBound) {
//            unbindStepService();
//            //mIsBound = false;
//        }
//    }
//
    public void onResume(Message msg) {
        onStateChanged(null);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



//    private void startStepService() {
//        if (!mIsRunning) {
//            Logger.i(TAG, "startStepService");
//            mIsRunning = true;
//            startService(new Intent(PedometerActivity.this,
//                    StepService.class));
//        }
//    }

    private void sendMessageStart() {
        Logger.d(TAG, "start sendMessageStart");
        if (!mIsBound)
            return;

        Logger.d(TAG, "sendMessageStart");

        Message msg = Message.obtain();
        msg.what = ServiceEvent.START;
        msg.replyTo = mCallbackMessenger;

        Bundle data = new Bundle();
        data.putString(ServiceEvent.NOTIFY_MESSAGE, "Step detector is running");

        msg.setData(data);

        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mIsRunning = true;
        onStateChanged(null);
    }

    private void sendMessageStop() {
        Logger.d(TAG, "start sendMessageStop");
        if (!mIsBound)
            return;

        Logger.d(TAG, "sendMessageStop");

        Message msg = Message.obtain();
        msg.what = ServiceEvent.STOP;
        msg.replyTo = mCallbackMessenger;

        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mIsRunning = false;
        onStateChanged(null);
    }

//    private void sendMessagePause() {
//        if (!mIsBound)
//            return;
//
//        Message msg = Message.obtain();
//        msg.what = ServiceEvent.PAUSE;
//        msg.replyTo = mCallbackMessenger;
//
//        try {
//            mServiceMessenger.send(msg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
    private void sendMessageResume() {
        if (!mIsBound)
            return;

        Message msg = Message.obtain();
        msg.what = ServiceEvent.RESUME;
        msg.replyTo = mCallbackMessenger;

        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindStepService() {
        Logger.d(TAG, "bindStepService");
        bindService(new Intent(PedometerActivity.this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        //mIsRunning = true;
        //mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
    }

    private void unbindStepService() {
        Logger.d(TAG, "unbindStepService");
        unbindService(mConnection);
        //mIsRunning = false;
        mIsBound = false;
        //mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
    }

    @Override
    protected void onPause() {
        Logger.d(TAG, "onPause");
        super.onPause();
//        sendMessagePause();

        mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);

        if (mIsBound) {
            unbindStepService();
//            mIsBound = false;
        }
        //mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
    }

    @Override
    protected void onResume() {
        Logger.d(TAG, "[ACTIVITY] onResume");
        super.onResume();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);

        //mPedometerSettings.clearServiceRunning();
        mIsRunning = mPedometerSettings.isServiceRunning();

        // Read from preferences if the service was running on the last onPause
        //mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
//        if (!mIsRunning && mPedometerSettings.isLastPaused(60000)) {
//            //startStepService();
//            bindStepService();
//        if (!mIsBound) {
//            bindStepService();
//        }
        //sendMessageResume();

        if (!mIsBound) {
            //mNeedResume = true;
            bindStepService();
        }

        onStateChanged(null);
        //mPedometerSettings.clearServiceRunning();
    }
}
