package com.intrafab.medicus.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;
import com.intrafab.medicus.R;
import com.intrafab.medicus.pedometer.OnEventUpdateCalories;
import com.intrafab.medicus.pedometer.OnEventUpdateSpeed;
import com.intrafab.medicus.pedometer.OnEventUpdateSteps;
import com.intrafab.medicus.pedometer.OnEventUpdateStepsGoal;
import com.intrafab.medicus.pedometer.OnEventUpdateTime;
import com.intrafab.medicus.utils.EventBus;
import com.squareup.otto.Subscribe;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class PlaceholderPedometerTodayFragment extends Fragment implements View.OnClickListener {

    private WheelIndicatorView wheelIndicatorView;
    private ImageButton mButtonPlayPause;

    private ImageButton mButtonStepsMode;
    private ImageButton mButtonDistanceMode;

    private ImageView mImageViewCalories;
    private ImageView mImageViewTimer;
    private ImageView mImageViewSpeedometer;

    private ImageView mImageViewArrowUp;

    private LinearLayout mLayoutStatistics;

    private TextView mTextViewSteps;
    private TextView mTextViewStepsGoal;
    private TextView mTextViewSpeed;
    private TextView mTextViewTime;
    private TextView mTextViewCalories;

    public interface OnClickListener {
        void onPedometerStarted();
        void onPedometerPaused();
        void onOpenStatisticsToday();
        void onStepModeActivated();
        void onDistanceModeActivated();
    }

    private OnClickListener mListener;

    public PlaceholderPedometerTodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pedometer_today, container, false);
        wheelIndicatorView = (WheelIndicatorView) rootView.findViewById(R.id.wheel_indicator_view);
        mButtonPlayPause = (ImageButton) rootView.findViewById(R.id.btnPlayPause);

        mButtonStepsMode = (ImageButton) rootView.findViewById(R.id.ivStepsMode);
        mButtonDistanceMode = (ImageButton) rootView.findViewById(R.id.ivDistanceMode);

        mImageViewCalories = (ImageView) rootView.findViewById(R.id.ivCalories);
        mImageViewTimer = (ImageView) rootView.findViewById(R.id.ivTimer);
        mImageViewSpeedometer = (ImageView) rootView.findViewById(R.id.ivSpeedometer);

        mImageViewArrowUp = (ImageView) rootView.findViewById(R.id.ivArrowUp);

        mLayoutStatistics = (LinearLayout) rootView.findViewById(R.id.layoutBottom);

        mTextViewSteps = (TextView) rootView.findViewById(R.id.tvCurrentSteps);
        mTextViewStepsGoal = (TextView) rootView.findViewById(R.id.tvStepsGoal);
        mTextViewSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
        mTextViewTime = (TextView) rootView.findViewById(R.id.tvTime);
        mTextViewCalories = (TextView) rootView.findViewById(R.id.tvCalories);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WheelIndicatorItem item = new WheelIndicatorItem(1.8f, Color.parseColor("#ff9000"));

        wheelIndicatorView.addWheelIndicatorItem(item);
        wheelIndicatorView.setFilledPercent(65);
        wheelIndicatorView.startItemsAnimation();
        //wheelIndicatorView.notifyDataSetChanged();

        mButtonPlayPause.setColorFilter(Color.parseColor("#fbc02d"), PorterDuff.Mode.MULTIPLY);
        mButtonPlayPause.setTag("pause");
        mButtonPlayPause.setOnClickListener(this);

        mButtonStepsMode.setOnClickListener(this);
        mButtonDistanceMode.setOnClickListener(this);
        mButtonStepsMode.setColorFilter(Color.parseColor("#5b2796"), PorterDuff.Mode.MULTIPLY);
        mButtonDistanceMode.setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.MULTIPLY);

        mImageViewCalories.setColorFilter(Color.parseColor("#99ee6312"), PorterDuff.Mode.MULTIPLY);
        mImageViewTimer.setColorFilter(Color.parseColor("#994f9800"), PorterDuff.Mode.MULTIPLY);
        mImageViewSpeedometer.setColorFilter(Color.parseColor("#9933ccff"), PorterDuff.Mode.MULTIPLY);

        mImageViewArrowUp.setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.MULTIPLY);

        mLayoutStatistics.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnPlayPause:
                String tag = (String) mButtonPlayPause.getTag();
                if (tag.equals("pause")) {
                    mButtonPlayPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    mButtonPlayPause.setTag("play");
                    if (mListener != null) {
                        mListener.onPedometerStarted();
                    }
                } else {
                    mButtonPlayPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    mButtonPlayPause.setTag("pause");
                    if (mListener != null) {
                        mListener.onPedometerPaused();
                    }
                }
                mButtonPlayPause.setColorFilter(Color.parseColor("#fbc02d"), PorterDuff.Mode.MULTIPLY);
                break;
            case R.id.ivStepsMode:
                mButtonStepsMode.setColorFilter(Color.parseColor("#5b2796"), PorterDuff.Mode.MULTIPLY);
                mButtonDistanceMode.setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.MULTIPLY);
                if (mListener != null) {
                    mListener.onStepModeActivated();
                }
                break;
            case R.id.ivDistanceMode:
                mButtonStepsMode.setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.MULTIPLY);
                mButtonDistanceMode.setColorFilter(Color.parseColor("#5b2796"), PorterDuff.Mode.MULTIPLY);
                if (mListener != null) {
                    mListener.onDistanceModeActivated();
                }
                break;
            case R.id.layoutBottom:
                if (mListener != null) {
                    mListener.onOpenStatisticsToday();
                }

//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations( R.anim.abc_slide_in_top, R.anim.abc_slide_out_top ) // Top Fragment Animation
//                        .hide(this)
//                        .setCustomAnimations( R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom ) // Bottom Fragment Animation
//                        .show( new PlaceholderPedometerTodayStatisticsFragment() )
//                        .commit();
                break;
        }
    }

    @Subscribe
    public void onStepsChanged(OnEventUpdateSteps event) {
        long value = event.steps < 0 ? 0 : event.steps;
        mTextViewSteps.setText(String.format("%1$,d", value));
    }

    @Subscribe
    public void onStepsGoalChanged(OnEventUpdateStepsGoal event) {
        long value = event.goal < 0 ? 0 : event.goal;
        mTextViewStepsGoal.setText(String.format("%1$,d", value));
    }

    @Subscribe
    public void onSpeedChanged(OnEventUpdateSpeed event) {
        double value = event.speed < 0.0001f ? 0f : event.speed;
        mTextViewSpeed.setText(String.format("%1$,.0f", value));
    }

    @Subscribe
    public void onTimeChanged(OnEventUpdateTime event) {
        long value = event.time < 0 ? 0 : event.time;
        mTextViewTime.setText(String.format("%d", value));
    }

    @Subscribe
    public void onCaloriesChanged(OnEventUpdateCalories event) {
        double value = event.calories < 0.0001f ? 0f : event.calories;
        mTextViewCalories.setText(String.format("%1$,.0f", value));
    }
}