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
import com.intrafab.medicus.pedometer.IStepCallback;
import com.intrafab.medicus.pedometer.OnEventUpdateCalories;
import com.intrafab.medicus.pedometer.OnEventUpdateDistance;
import com.intrafab.medicus.pedometer.OnEventUpdatePace;
import com.intrafab.medicus.pedometer.OnEventUpdateSpeed;
import com.intrafab.medicus.pedometer.OnEventUpdateStep;
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
        PlaceholderPedometerTodayFragment.OnClickListener,
        IStepCallback {
    private static final String TAG = PedometerActivity.class.getName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SharedPreferences mSettings;
    private Settings mPedometerSettings;

    private boolean mIsRunning;

    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder) service).getService();

            mService.registerCallback(PedometerActivity.this);
            mService.loadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private static final int MESSAGE_STEPS = 1;
    private static final int MESSAGE_PACE = 2;
    private static final int MESSAGE_DISTANCE = 3;
    private static final int MESSAGE_SPEED = 4;
    private static final int MESSAGE_CALORIES = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STEPS:
                    EventBus.getInstance().post(new OnEventUpdateStep(msg.arg1));
                    break;
                case MESSAGE_PACE:
                    EventBus.getInstance().post(new OnEventUpdatePace(msg.arg1));
                    break;
                case MESSAGE_DISTANCE:
                    EventBus.getInstance().post(new OnEventUpdateDistance(msg.arg1 / 1000f));
                    break;
                case MESSAGE_SPEED:
                    EventBus.getInstance().post(new OnEventUpdateSpeed(msg.arg1 / 1000f));
                    break;
                case MESSAGE_CALORIES:
                    EventBus.getInstance().post(new OnEventUpdateCalories(msg.arg1 / 1f));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

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
    }

    @Override
    protected void onStop() {
        stopStepService();
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

    }

    @Override
    public void onPedometerPaused() {

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

    @Override
    public void onPaceChanged(long value) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_PACE, (int) value, 0));
    }

    @Override
    public void onCaloriesChanged(double value) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_CALORIES, (int) value, 0));
    }

    @Override
    public void onDistanceChanged(float value) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_DISTANCE, (int) (value * 1000f), 0));
    }

    @Override
    public void onSpeedChanged(float value) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SPEED, (int) (value * 1000f), 0));
    }

    @Override
    public void onStepChanged(long value) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_STEPS, (int) value, 0));
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

    @Override
    protected void onResume() {
        Logger.i(TAG, "[ACTIVITY] onResume");
        super.onResume();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);

        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isLastPaused(60000)) {
            startStepService();
            bindStepService();
        } else if (mIsRunning) {
            bindStepService();
        }

        mPedometerSettings.clearServiceRunning();


//        mIsMetric = mPedometerSettings.isMetric();
//        ((TextView) findViewById(R.id.distance_units)).setText(getString(
//                mIsMetric
//                        ? R.string.kilometers
//                        : R.string.miles
//        ));
//        ((TextView) findViewById(R.id.speed_units)).setText(getString(
//                mIsMetric
//                        ? R.string.kilometers_per_hour
//                        : R.string.miles_per_hour
//        ));
//
//        mMaintain = mPedometerSettings.getMaintainOption();
//        ((LinearLayout) this.findViewById(R.id.desired_pace_control)).setVisibility(
//                mMaintain != PedometerSettings.M_NONE
//                        ? View.VISIBLE
//                        : View.GONE
//        );
//        if (mMaintain == PedometerSettings.M_PACE) {
//            mMaintainInc = 5f;
//            mDesiredPaceOrSpeed = (float) mPedometerSettings.getDesiredPace();
//        } else if (mMaintain == PedometerSettings.M_SPEED) {
//            mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
//            mMaintainInc = 0.1f;
//        }
//        Button button1 = (Button) findViewById(R.id.button_desired_pace_lower);
//        button1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mDesiredPaceOrSpeed -= mMaintainInc;
//                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
//                displayDesiredPaceOrSpeed();
//                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
//            }
//        });
//        Button button2 = (Button) findViewById(R.id.button_desired_pace_raise);
//        button2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mDesiredPaceOrSpeed += mMaintainInc;
//                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
//                displayDesiredPaceOrSpeed();
//                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
//            }
//        });
//        if (mMaintain != PedometerSettings.M_NONE) {
//            ((TextView) findViewById(R.id.desired_pace_label)).setText(
//                    mMaintain == PedometerSettings.M_PACE
//                            ? R.string.desired_pace
//                            : R.string.desired_speed
//            );
//        }
//
//
//        displayDesiredPaceOrSpeed();
    }

    private void startStepService() {
        if (!mIsRunning) {
            Logger.i(TAG, "startStepService");
            mIsRunning = true;
            startService(new Intent(PedometerActivity.this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        Logger.i(TAG, "bindStepService");
        bindService(new Intent(PedometerActivity.this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Logger.i(TAG, "unbindStepService");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Logger.i(TAG, "stopStepService");
        if (mService != null) {
            Logger.i(TAG, "stopStepService");
            stopService(new Intent(PedometerActivity.this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    @Override
    protected void onPause() {
        Logger.i(TAG, "onPause");
        super.onPause();
        if (mIsRunning) {
            unbindStepService();
        }
        mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);


    }
}
