package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public final class SimpleFilter {

    private float mX0; // predicted state
    private float mP0; // predicted covariance
    private float mF; // factor of real value to previous real value
    private float mH; // factor of measured value to real value
    private float mQ; // measurement noise
    private float mR; // environment noise
    private float mState = 0; // current state
    private float mCovariance = 0.1f; // current covariance

    public SimpleFilter() {
        mF = 0f;
        mH = 0f;
        mQ = 0f;
        mR = 0f;
    }

    public SimpleFilter(float f, float h, float q, float r) {
        mF = f;
        mH = h;
        mQ = q;
        mR = r;
    }

    public void init(float initialState, float initialCovariance) {
        mState = initialState;
        mCovariance = initialCovariance;
    }

    public float correct(float measuredValue) {
        // time update - prediction
        mX0 = mF * mState;
        mP0 = mF * mCovariance * mF + mQ;

        // measurement update - correction
        float k = mH * mP0 / (mH * mP0 * mH + mR);
        mCovariance = (1 - k * mH) * mP0;
        return mState = mX0 + k * (measuredValue - mH * mX0);
    }
}
